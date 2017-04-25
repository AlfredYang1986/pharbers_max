package com.pharbers.aqll.alcalc.almain

import java.io.File
import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.RoundRobinPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alcalc.alcmd.pkgcmd.{pkgCmd, unPkgCmd}
import com.pharbers.aqll.alcalc.alcmd.scpcmd.scpCmd
import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alemchat.sendMessage
import com.pharbers.aqll.alcalc.alfinaldataprocess.alRestoreColl
import com.pharbers.aqll.alcalc.alfinaldataprocess.alWeightSum._
import com.pharbers.aqll.alcalc.aljobs.{alJob, alPkgJob}
import com.pharbers.aqll.alcalc.aljobs.alJob._
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{calc_final_result, calc_need_files, check_excel_jobs, concert_groupjust_result, _}
import com.pharbers.aqll.alcalc.almaxdefines.{alCalcParmary, alMaxProperty, endDate, startDate}
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alcalc.almodel.IntegratedData
import com.pharbers.aqll.alcalc.mail.{Mail, MailAgent, MailToEmail}
import com.pharbers.aqll.util.{GetProperties, StringOption}
import com.pharbers.aqll.util.dao._data_connection_cores

import scala.concurrent.Await
import scala.concurrent.stm.atomic
import scala.concurrent.stm.Ref
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alcalc.alfinaldataprocess.alSampleCheck
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.util.GetProperties._

/**
  * Created by Alfred on 10/03/2017.
  */
object alMaxDriver {
    def props = Props[alMaxDriver]
    def name = "driver-actor"
}

class alMaxDriver extends Actor
                     with ActorLogging
                     with alMaxJobsSchedule
                     with alGroupJobsSchedule
                     with alCreateExcelSplitRouter
                     with alGroupJobsManager 
                     with alCalcJobsSchedule
                     with alCalcJobsManager
                     with alPkgJob {
    val start = startDate()
    implicit val t = Timeout(0.5 second)

    override def receive = {
        case worker_register() =>
            log.info("worker_register")
            atomic { implicit txn => server_info.section() = server_info.section() + 1 }
        case group_register(a) => registerGroupRouter(a)
        case filter_excel_jobs(file, parmary, act) => {
            val cj = max_filter_excel_jobs(file)
            cj.result
            val lst = Option(cj.cur.get.storages.head.asInstanceOf[alStorage])
            lst match {
                case None => println("File is None")
                case Some(x) =>
                    x.doCalc
                    val p = x.data.asInstanceOf[List[IntegratedData]].map( x => (x.getYearAndmonth.toString.substring(0, 4), x.getMarket1Ch)).distinct
                    x.isCalc = false
                    p.size match {
                        case 0 => println("this is File is None")
                        case 1 =>
                            parmary.year = p.head._1.toInt
                            parmary.market = StringOption.takeStringSpace(p.head._2)
                            act ! push_max_job(file, parmary)
                        case n if n > 1 => println("需要分拆文件，再次读取")
                        case _ => ???
                    }
            }
        }
        case push_max_job(file_path, p) => {
            println(s"sign a job with file name $file_path")
            atomic { implicit txn =>
                jobs() = jobs() :+ max_jobs(file_path)
                jobs2() = jobs2() :+ (max_jobs(file_path), p)
            }
        }
        case schedule_jobs() => {
            atomic { implicit txn =>
                jobs2() match {
                    case head :: lst => {
                        val f = excel_split_router ? split_job(head._1, head._2)
                        Await.result(f, 0.5 seconds) match {
                            case spliting_busy() => Unit
                            case spliting_job(_,_) => jobs2() = jobs2().tail
                        }
                    }
                    case Nil => Unit
                }
//                jobs() match {
//                    case head :: lst => {
//                        val f = excel_split_router ? split_job(head)
//                        Await.result(f, 0.5 seconds) match {
//                            case spliting_busy() => Unit
//                            case spliting_job(_) => jobs() = jobs().tail
//                        }
//                    }
//                    case Nil => Unit
//                }
            }
        }
        case finish_max_job(uuid) => {
            println(s"finish a job with uuid $uuid")
        }
        case finish_split_excel_job(p, j, c) => {
            atomic {implicit  thx =>
                alCalcParmary.alParmary().append(c)
            }

            val subs = j map (x => alMaxProperty(p, x, Nil))
            pushGroupJobs(alMaxProperty(null, p, subs))

            // TODO : 最开始的Split的文件 传输到各个机器上
            cur = Some(new pkgCmd(s"${GetProperties.memorySplitFile}${GetProperties.sync}$p" :: Nil, s"${GetProperties.memorySplitFile}${GetProperties.fileTarGz}$p")
                        :: new scpCmd(s"${GetProperties.memorySplitFile}${GetProperties.fileTarGz}$p.tar.gz", "program/scp/", "aliyun106", "root")
                        :: new scpCmd(s"${GetProperties.memorySplitFile}${GetProperties.fileTarGz}$p.tar.gz", "program/scp/", "aliyun50", "root")
                        :: Nil)
            process = do_pkg() :: Nil
            super.excute()
        }
        case schedule_group() => scheduleOneGroupJob
        case group_result(uuid, sub_uuid) => successWithGroup(uuid, sub_uuid)
       
        case calc_register(a) => registerCalcRouter(a)
        case push_calc_job(p) => pushCalcJobs(p)
        case schedule_calc() => scheduleOneCalcJob
        case calc_sum_result(uuid, sub_uuid, sum) => sumSuccessWithWork(uuid, sub_uuid, sum)
        case calc_final_result(uuid, sub_uuid, v, u) => finalSuccessWithWork(uuid, sub_uuid, v, u, start)
        case db_final_result(uuid, dbuuid) => dbfinalSuccessWithWork(uuid, dbuuid, start)
        case commit_finalresult_jobs(company) => commit_finalresult_jobs_func(company)
        case check_excel_jobs(company,filename) => check_excel_jobs_func(company,filename)
        case x : Any => {
            println(x)
            ???
        }
    }

    val excel_split_router = CreateExcelSplitRouter
}

trait alMaxJobsSchedule { this : Actor =>
    val jobs = Ref(List[alJob]())       // only unhandled jobs
    val jobs2 = Ref(List[(alJob, alCalcParmary)]())       // only unhandled jobs 带参数的jobs
    val timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_jobs)
}

trait alGroupJobsSchedule { this : Actor =>
    val waiting_grouping = Ref(List[alMaxProperty]())     // only for waiting jobs
    val grouping_jobs = Ref(List[alMaxProperty]())     // only for calcing jobs
    val group_timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_group)
}

trait alCreateExcelSplitRouter { this : Actor =>
    def CreateExcelSplitRouter =
        context.actorOf(RoundRobinPool(1).props(alExcelSplitActor.props), name = "excel-split-router")
}

trait alGroupJobsManager extends alPkgJob { this : Actor with alGroupJobsSchedule =>
    val group_router = Ref(List[ActorRef]())
    var group_nodenumber = -1

    def registerGroupRouter(a : ActorRef) = atomic { implicit txn =>
            group_router() = group_router() :+ a
        }
    
    def pushGroupJobs(cur : alMaxProperty) = atomic { implicit txn =>
            waiting_grouping() = waiting_grouping() :+ cur
        }
   
    def scheduleOneGroupJob = atomic { implicit txn =>
            waiting_grouping() match {
                case head :: lst => {
                    if (canSignGroupJob(head))
                        signGroupJob(head)
                    }
                case Nil => Unit
            }
        }
    
    def successWithGroup(uuid : String, sub_uuid : String) = {
        grouping_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
            case None => Unit
            case Some(r) => {
                r.subs.find (x => x.uuid == sub_uuid).map (x => x.grouped = true).getOrElse(Unit)

                // TODO : 解压汇总过来的Group文件
                cur = Some(new unPkgCmd(s"/root/program/scp/$sub_uuid", "/root/program/") :: Nil)
                process = do_pkg() :: Nil
                super.excute()

                if (r.subs.filterNot (x => x.grouped).isEmpty) {
                    val common = common_jobs()
//                    common.cur = Some(alStage(r.subs map (x => s"config/group/${x.uuid}")))
//                    val a = r.subs map(_.uuid)



                    common.cur = Some(alStage(r.subs map (x => s"${GetProperties.memorySplitFile}${GetProperties.group}${x.uuid}")))
                    common.process = restore_grouped_data() ::
                        do_calc() :: do_union() :: do_calc() ::
                        do_map (alShareData.txt2IntegratedData(_)) :: do_calc() :: Nil

                    common.result

                    val concert = common.cur.get.storages.head.asInstanceOf[alStorage]
                    val m = alStorage.groupBy (x =>
                        (x.asInstanceOf[IntegratedData].getYearAndmonth, x.asInstanceOf[IntegratedData].getMinimumUnitCh)
                    )(concert)

                    val g = alStorage(m.values.map (x => x.asInstanceOf[alStorage].data.head.toString).toList)
                    g.doCalc
                    val sg = alStage(g :: Nil)
                    val pp = presist_data(Some(r.uuid), Some("group"))
                    pp.precess(sg)

                    println("done for grouping")

                    groupJobSuccess(uuid)
                }
            }
        }
    }
    
    def groupJobSuccess(uuid : String) = {
        grouping_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
            case None => Unit
            case Some(r) => {
                atomic { implicit tnx =>
                    grouping_jobs() = grouping_jobs().tail
                }
                // 分拆计算文件
                val spj = split_group_jobs(Map(split_group_jobs.max_uuid -> r.uuid))
                val (p, sb) = spj.result.map (x => x.asInstanceOf[(String, List[String])]).getOrElse(throw new Exception("split grouped error"))
                val subs = sb map (x => alMaxProperty(p, x, Nil))

                // TODO : 压缩最终需要用到的Group文件
                println(s"calc is uuid = $uuid")
                cur = Some(new pkgCmd(s"${GetProperties.memorySplitFile}${GetProperties.calc}$uuid" :: Nil, s"${GetProperties.memorySplitFile}${GetProperties.fileTarGz}$uuid")
                            :: new scpCmd(s"${GetProperties.memorySplitFile}${GetProperties.fileTarGz}$uuid.tar.gz", s"${GetProperties.scpPath}", "aliyun106", "root")
                            :: new scpCmd(s"${GetProperties.memorySplitFile}${GetProperties.fileTarGz}$uuid.tar.gz", s"${GetProperties.scpPath}", "aliyun50", "root")
                            :: Nil)
                process = do_pkg() :: Nil
                super.excute()

                self ! push_calc_job(alMaxProperty(null, p, subs))
            }
        }
    }
       
    def groupJobFailed(uuid : String) = {
        
    }
    
    def canSignGroupJob(p : alMaxProperty): Boolean = {
        implicit val t = Timeout(0.5 second)
        val f = group_router.single.get map (x => x ? can_sign_job())
        p.subs.length <= (f.map (x => Await.result(x, 0.5 seconds)).count(x => x.isInstanceOf[sign_job_can_accept]))
    }

    def signGroupJob(p : alMaxProperty) = {
        // TODO: sign with 递归
        siginEach(group_router.single.get)
        atomic { implicit tnx =>
            waiting_grouping() = waiting_grouping().tail
            grouping_jobs() = grouping_jobs() :+ p
        }

        def siginEach(lst: List[ActorRef]): Unit = {
            lst match {
                case Nil => println("not enough group to do the jobs")
                case node => {
                    group_nodenumber = group_nodenumber + 1
                    lst.head ! concert_groupjust_result(group_nodenumber)
                    lst.head ! group_job(p)
                    siginEach(lst.tail)
                }
                case _ => ???
            }
        }
    }
}

trait alCalcJobsSchedule { this : Actor =>
    val waiting_calc = Ref(List[alMaxProperty]())     // only for waiting jobs
    val calcing_jobs = Ref(List[alMaxProperty]())     // only for calcing jobs
    val calc_timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_calc)
}

trait alCalcJobsManager extends alPkgJob { this : Actor with alCalcJobsSchedule =>
    val calc_router = Ref(List[ActorRef]())
    var calc_nodenumber = -1
    var section_number = -1

    def registerCalcRouter(a : ActorRef) = atomic { implicit txn =>
            calc_router() = calc_router() :+ a
        }
    
    def pushCalcJobs(cur : alMaxProperty) = atomic { implicit txn =>
            waiting_calc() = waiting_calc() :+ cur
        }
   
    def scheduleOneCalcJob = atomic { implicit txn =>
            waiting_calc() match {
                case head :: lst => {
                    if (canSignCalcJob(head))
                        signCalcJob(head)
                    }
                case Nil => Unit
            }
        }

    def canSignCalcJob(p : alMaxProperty): Boolean = {
        implicit val t = Timeout(0.5 second)
        val f = calc_router.single.get map (x => x ? can_sign_job())
        p.subs.length / server_info.cpu <= (f.map (x => Await.result(x, 0.5 seconds)).count(x => x.isInstanceOf[sign_job_can_accept]))
        // TODO : 每个四核，这里要改
    }

    def signCalcJob(p : alMaxProperty) = {
        // TODO: sign with 递归
        atomic { implicit tnx =>
            siginEach(calc_router.single.get)
            waiting_calc() = waiting_calc().tail
            calcing_jobs() = calcing_jobs() :+ p
        }

        def siginEach(lst: List[ActorRef]): Unit = {
            lst match {
                case Nil => println("not enough calc to do the jobs")
                case node => {
                    //TODO:Calc
                    calc_nodenumber = calc_nodenumber + 1
                    lst.head ! concert_calcjust_result(calc_nodenumber)
                    alCalcParmary.alParmary.single.get.find(_.uuid == p.uuid) match {
                        case None => println("not CalcParamry file")
                        case Some(x) =>
                            lst.head ! calc_job(p, x)
                            siginEach(lst.tail)
                    }
                }
                case _ => ???
            }
        }
    }

    def sumSuccessWithWork(uuid : String, sub_uuid : String, sum : List[(String, (Double, Double, Double))]) = {
        import scala.math.BigDecimal
        calcing_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
            case None => Unit
            case Some(r) => {

                println(s"sum in singleton $sum with $sub_uuid")

                r.subs.find (x => x.uuid == sub_uuid).map { x =>
                    x.isSumed = true
                    x.sum = sum
                }.getOrElse(Unit)

                if (r.subs.filterNot (x => x.isSumed).isEmpty) {
                    val tmp = r.subs.map (x => x.sum).flatten
                    println(s"done for suming ${tmp.filter(_._1 == "98")}")
                    r.sum = (tmp.groupBy(_._1) map { x =>
                        (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
                    }).toList
                    r.isSumed = true
                    println(s"done for suming ${r.sum}")

                    val mapAvg = r.sum.map { x =>
                        (x._1, (BigDecimal((x._2._1 / x._2._3).toString).toDouble),(BigDecimal((x._2._2 / x._2._3).toString).toDouble))
//                        (x._1, (x._2._1 / x._2._3),(x._2._2 / x._2._3))
                    }

                    calc_router.single.get foreach ( x => x ! calc_avg_job(r.uuid, mapAvg))
                }
            }
        }
    }

    // TODO : 这段暂时没用到，不过在解决入库效率上，可能会用到此方法
    def dbfinalSuccessWithWork(uuid : String, dbuuid: String, start: Long) = {
        println(s"section_number = $section_number")
        // TODO : 数据库高速还原
        val company = alCalcParmary.alParmary.single.get.find(_.uuid == uuid) match {
            case None =>
                println(s"not company")
                //alRestoreColl("", sub_uuid :: Nil)
                ("", "")
            case Some(x) =>
                val u = x.company+UUID.randomUUID().toString
                alRestoreColl(u, dbuuid :: Nil)
                (x.company, u)
        }

        section_number = section_number + 1
        println(s"section_number = $section_number")

        if(section_number == 2) {
            section_number = -1
            // TODO : 数据去重，重新入库
            println(s"开始去重数据")
            val tmp = (new alWeightSum(company._1, company._2))
            println(s"done calc job with uuid ${uuid}, final value : ${tmp.f_sales_sum2} and final unit : ${tmp.f_units_sum2}")
            println(s"结束去重数据")
            endDate("入库完成",start)
        }
    }

    def finalSuccessWithWork(uuid : String, sub_uuid : String, v : Double, u : Double , start: Long) = {
        calcing_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
            case None => Unit
            case Some(r) => {
                r.subs.find (x => x.uuid == sub_uuid).map { x =>
                    x.isCalc = true
                    x.finalValue = v
                    x.finalUnit = u
                }.getOrElse(Unit)

                // TODO : 数据库高速还原
                val company = alCalcParmary.alParmary.single.get.find(_.uuid == uuid) match {
                    case None =>
                        println(s"not company")
                        //alRestoreColl("", sub_uuid :: Nil)
                        ("", "", "")
                    case Some(x) =>
                        val u = x.company+uuid
                        alRestoreColl(u, sub_uuid :: Nil)
                        (x.company, u, x.uname)
                    case _ => ???
                }

                sendMessage.send(uuid, company._1, 2, company._3)

                if (r.subs.filterNot (x => x.isCalc).isEmpty) {
                    sendMessage.send(uuid, company._1, 10, company._3)
                    r.finalValue = r.subs.map(_.finalValue).sum
                    r.finalUnit = r.subs.map(_.finalUnit).sum
                    r.isCalc = true
                    println(s"done calc job with uuid ${r.uuid}, final value : ${r.finalValue} and final unit : ${r.finalUnit}")
                    // TODO : 数据去重，重新入库
//                    println(s"开始去重数据")
//                    val tmp = (alWeightSum(company._1, company._2))
//                    println(s"done calc job with uuid ${uuid}, final value : ${tmp.f_sales_sum2} and final unit : ${tmp.f_units_sum2}")
//                    println(s"结束去重数据")

                    val e_mail = MailToEmail.getEmail(company._1)
                    MailAgent(Mail(GetProperties.mail_context, GetProperties.mail_subject, e_mail)).sendMessage()
                    endDate("计算完成",start)
                    sendMessage.send(uuid, company._1, 100, company._3)
                    atomic { implicit tnx =>
                        calcing_jobs() = calcing_jobs().tail
                    }
                }
            }
        }
    }

    def commit_finalresult_jobs_func(company: String) = {
        alCalcParmary.alParmary.single.get.find(_.company.equals(company)) match {
            case None => println(s"commit_finalresult_jobs_func not company")
            case Some(x) =>
                sendMessage.send("", company, 30, x.uname)
                println(s"x.uuid = ${x.uuid}")
                new alWeightSum(company, company + x.uuid)
                sendMessage.send("", company, 20, x.uname)
                println(s"开始删除临时表")
                _data_connection_cores.getCollection(company + x.uuid).drop()
                println(s"结束删除临时表")
                val index = alCalcParmary.alParmary.single.get.indexWhere(_.uuid.equals(x.uuid))
                alCalcParmary.alParmary.single.get.remove(index)
                sendMessage.send("", company, 100, x.uname)
        }
    }

    def check_excel_jobs_func(company: String,filename: String) = {
        alCalcParmary.alParmary.single.get.find(_.company.equals(company)) match {
            case None => println(s"commit_finalresult_jobs_func not company")
            case Some(x) => println(x.company)
        }
        //println(s"company=${company} filename=${filename}")
        //println("11111111111")
        //alSampleCheck(company,filename)
    }
}
