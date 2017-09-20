package com.pharbers.aqll.alCalc.almain


import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.stm.atomic
import scala.concurrent.duration._
import com.pharbers.aqll.alCalcEnergy._
import com.pharbers.aqll.common.alCmd.pkgcmd.pkgCmd
import com.pharbers.aqll.common.alCmd.scpcmd.scpCmd
import com.pharbers.aqll.common.alString.alStringOpt._
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.serverConfig._
import com.pharbers.aqll.alCalcMemory.aljobs.alPkgJob
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty, startDate}
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring.alAkkaMonitor._
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.{max_filter_excel_jobs, max_jobs}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.do_pkg
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.server_info

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
        
        case crash_group(u, m) => {
//            group_router.single.get foreach (x => x ! clean_crash_actor(u))
            groupRouter foreach (x => x ! clean_crash_actor(u))
        }
        case crash_calc(u, m) => {
//            calc_router.single.get foreach (x => x ! clean_crash_actor(u))
            calcRouter foreach (x => x ! clean_crash_actor(u))
        }
        case worker_register() =>
            log.info("worker_register")
            atomic { implicit txn => server_info.section() = server_info.section() + 1 }
            
//        case group_register(a) => registerGroupRouter(a)
        
        case filter_excel_jobs(file, parmary, act) => {
            val cj = max_filter_excel_jobs(file)
            cj.result
            val lst = Option(cj.cur.get.storages.head.asInstanceOf[alStorage])
            lst match {
                case None => log.info("File is None")
                case Some(x) =>
                    x.doCalc
                    val p = x.data.asInstanceOf[List[IntegratedData]].filterNot(x => x.getYearAndmonth ==0 && !x.getMarket1Ch.isEmpty).map( x => (x.getYearAndmonth.toString.substring(0, 4), x.getMarket1Ch)).distinct
                    x.isCalc = false
                    p.size match {
                        case 1 =>
                            parmary.year = p.head._1.toInt
                            parmary.market = removeSpace(p.head._2)
                            act ! push_max_job(file, parmary)
                        case n if n > 1 => log.info("需要分拆文件，再次读取")
                        case _ => ???
                    }
            }
        }
        case push_max_job(file_path, p) => {
            log.info(s"sign a job with file name $file_path")
            atomic { implicit txn =>
                jobs() = jobs() :+ (max_jobs(file_path), p)
            }
        }
        case schedule_jobs() => {
            atomic { implicit txn =>
                jobs() match {
                    case head :: lst => {
                        val f = excel_split_router ? split_job(head._1, head._2)
                        Await.result(f, 0.5 seconds) match {
                            case spliting_busy() => Unit
                            case spliting_job(_,_) => jobs() = jobs().tail
                        }
                    }
                    case Nil => Unit
                }
            }
        }
        case finish_max_group_job(uuid) => {
            group_nodenumber = -1
            log.info(s"finish a group job with uuid $uuid")
        }
        case finish_max_job(uuid) => {
            calc_nodenumber = -1
            log.info(s"finish a job with uuid $uuid")
        }
        case finish_split_excel_job(p, j, c) => {
            atomic {implicit  thx =>
                alCalcParmary.alParmary().append(c)
            }

            val subs = j map (x => alMaxProperty(p, x, Nil))
            pushGroupJobs(alMaxProperty(null, p, subs))

            cur = Some(pkgCmd(s"${memorySplitFile}${sync}$p" :: Nil, s"${memorySplitFile}${fileTarGz}$p")
                        :: scpCmd(s"${memorySplitFile}${fileTarGz}$p.tar.gz", s"${scpPath}", serverHost106, serverUser)
                        :: scpCmd(s"${memorySplitFile}${fileTarGz}$p.tar.gz", s"${scpPath}", serverHost50, serverUser)
                        :: Nil)
            process = do_pkg() :: Nil
            super.excute()
        }
        case schedule_group() => scheduleOneGroupJob
        case group_result(uuid, sub_uuid) => successWithGroup(uuid, sub_uuid)
       
//        case calc_register(a) => registerCalcRouter(a)
        
        case push_calc_job(p) => pushCalcJobs(p)
        case schedule_calc() => scheduleOneCalcJob
        case calc_sum_result(uuid, sub_uuid, sum) => sumSuccessWithWork(uuid, sub_uuid, sum)
        case calc_final_result(uuid, sub_uuid, v, u) => finalSuccessWithWork(uuid, sub_uuid, v, u, start)
        case commit_finalresult_jobs(company, uuid) => commit_finalresult_jobs_func(company, uuid)
        case check_excel_jobs(company,filename) => check_excel_jobs_func(company,filename)
        case x : Any => {
            log.info(x.toString)
            ???
        }
    }

    val excel_split_router = CreateExcelSplitRouter
}
