package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, ActorSystem, Props, Scheduler}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.RoundRobinPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alcalc.aljobs.alJob
import com.pharbers.aqll.alcalc.aljobs.alJob._
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger._
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alcalc.almaxdefines.alMaxProperty
import com.pharbers.aqll.calc.split.{SplitAggregator, SplitGroupMaster}
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines._

import scala.concurrent.Await
import scala.concurrent.stm.atomic
import scala.concurrent.stm.Ref
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

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
                     with alCalcJobsManager {

    implicit val t = Timeout(0.5 second)
    override def receive = {
        case group_register(a) => registerGroupRouter(a)
        case push_max_job(file_path) => {
            println(s"sign a job with file name $file_path")
            atomic { implicit txn =>
                jobs() = jobs() :+ max_jobs(file_path)
            }
        }
        case schedule_jobs() => {
            atomic { implicit txn =>
                jobs() match {
                    case head :: lst => {
                        val f = excel_split_router ? split_job(head)
                        Await.result(f, 0.5 seconds) match {
                            case spliting_busy() => Unit
                            case spliting_job(_) => jobs() = jobs().tail
                        }
                    }
                    case Nil => Unit
                }
            }
        }
        case finish_max_job(uuid) => {
            println(s"finish a job with uuid $uuid")
        }
        case finish_split_excel_job(p, j) => {
            val subs = j map (x => alMaxProperty(p, x, Nil))
            pushGroupJobs(alMaxProperty(null, p, subs))
        }
        case schedule_group() => scheduleOneGroupJob
        case group_result(uuid, sub_uuid) => successWithGroup(uuid, sub_uuid)
       
        case calc_register(a) => registerCalcRouter(a)
        case push_calc_job(p) => pushCalcJobs(p)
        case schedule_calc() => scheduleOneCalcJob
        case calc_sum_result(uuid, sub_uuid, sum) => sumSuccessWithWork(uuid, sub_uuid, sum)
        
        case x : Any => {
            println(x)
            ???
        }
    }

    val excel_split_router = CreateExcelSplitRouter
}

trait alMaxJobsSchedule { this : Actor =>
    val jobs = Ref(List[alJob]())       // only unhandled jobs
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

trait alGroupJobsManager { this : Actor with alGroupJobsSchedule =>
    val group_router = Ref(List[ActorRef]())

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
                    
                if (r.subs.filterNot (x => x.grouped).isEmpty) {
                    val common = common_jobs()
                    common.cur = Some(alStage(r.subs map (x => s"config/group/${x.uuid}")))
                    common.process = restore_grouped_data() :: do_distinct() :: 
                                        do_calc() :: do_union() :: do_calc() :: 
                                        presist_data(Some(r.uuid), Some("group")) :: Nil
                    common.result
           
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
        group_router.single.get.head ! group_job(p)

        atomic { implicit tnx =>
            waiting_grouping() = waiting_grouping().tail
            grouping_jobs() = grouping_jobs() :+ p
        }
    }
}

trait alCalcJobsSchedule { this : Actor =>
    val waiting_calc = Ref(List[alMaxProperty]())     // only for waiting jobs
    val calcing_jobs = Ref(List[alMaxProperty]())     // only for calcing jobs
    val calc_timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_calc)
}

trait alCalcJobsManager { this : Actor with alCalcJobsSchedule =>
    val calc_router = Ref(List[ActorRef]())

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
        p.subs.length <= (f.map (x => Await.result(x, 0.5 seconds)).count(x => x.isInstanceOf[sign_job_can_accept]))
    }

    def signCalcJob(p : alMaxProperty) = {
        // TODO: sign with 递归
        calc_router.single.get.head ! calc_job(p)

        atomic { implicit tnx =>
            waiting_calc() = waiting_calc().tail
            calcing_jobs() = calcing_jobs() :+ p
        }
    }

    def sumSuccessWithWork(uuid : String, sub_uuid : String, sum : List[(String, (Double, Double, Double))]) = {
        calcing_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
            case None => Unit
            case Some(r) => {
                r.subs.find (x => x.uuid == sub_uuid).map { x =>
                    x.isSumed = true
                    x.sum = sum
                    r.sum = sum ++: sum
                }.getOrElse(Unit)

                if (r.subs.filterNot (x => x.isSumed).isEmpty) {
                    r.sum = (r.sum.groupBy(_._1) map { x =>
                        (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
                    }).toList
                    println(s"done for suming ${r.sum}")

                    // TODO : 开始计算平均值
                }
            }
        }
    }
}
