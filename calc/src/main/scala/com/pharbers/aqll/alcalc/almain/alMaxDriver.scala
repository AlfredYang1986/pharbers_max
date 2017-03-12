package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, ActorSystem, Props, Scheduler}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.{RoundRobinPool}
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alcalc.aljobs.alJob
import com.pharbers.aqll.alcalc.aljobs.alJob.common_jobs
import com.pharbers.aqll.alcalc.aljobs.alJob.max_jobs
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
                     with alCalcJobsSchedule
                     with alCreateExcelSplitRouter
                     with alCreateCalcRouter {

    implicit val t = Timeout(0.5 second)
    override def receive = {

        case worker_register(map) => {
            println(s"map = $map")
        }

        case calc_register(a) => {
            atomic { implicit txn =>
                calc_router() = calc_router() :+ a
            }
        }

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
            println(s"split excel $p and sub calc $j")
            val subs = j map (x => alMaxProperty(p, x, Nil))
            val cur = alMaxProperty(null, p, subs)
            atomic { implicit txn =>
                waiting_jobs() = waiting_jobs() :+ cur
            }
        }

        case schedule_calc() => {
            atomic { implicit txn =>
                waiting_jobs() match {
                    case head :: lst => {
                        println(head)
                        if (canSignJob(head))
                            signJob(head)
                    }
                    case Nil => Unit
                }
            }
        }

        // one mechine group success
        case group_result(uuid, sub_uuid) => {
            println("fuck")
            println("grouping uuid is $uuid")
            calcing_jobs.single.get.find(x => x.uuid == uuid).map (x => Some(x)).getOrElse(None) match {
                case None => Unit
                case Some(r) => {
                    println(s"current group end is $r")
                    r.subs.find (x => x.uuid == sub_uuid).map (x => x.grouped = true).getOrElse(Unit)
            
                    if (r.subs.filterNot (x => x.grouped).isEmpty) {
                        val common = common_jobs()
                        common.cur = Some(alStage(r.subs map (x => s"config/group/${x.uuid}")))
                        common.process = restore_grouped_data() :: do_distinct() :: 
                                            do_calc() :: do_union() :: do_calc() :: 
                                            presist_data(Some(r.uuid), Some("group")) :: Nil
                        common.result
           
                        println("done for grouping")
                    }
                }
            }
        }
        
        case _ => ???
    }

    val excel_split_router = CreateExcelSplitRouter
//    val calc_router = alCreateCalcRouter
}

trait alMaxJobsSchedule { this : Actor =>
    val jobs = Ref(List[alJob]())       // only unhandled jobs
    val timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_jobs)
}

trait alCalcJobsSchedule { this : Actor =>
    val waiting_jobs = Ref(List[alMaxProperty]())     // only for waiting jobs
    val calcing_jobs = Ref(List[alMaxProperty]())     // only for calcing jobs
    val calc_timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_calc)
}

trait alCreateExcelSplitRouter { this : Actor =>
    def CreateExcelSplitRouter =
        context.actorOf(RoundRobinPool(1).props(alExcelSplitActor.props), name = "excel-split-router")
}

trait alCreateCalcRouter { this : Actor with alCalcJobsSchedule =>
    val calc_router = Ref(List[ActorRef]())

    def canSignJob(p : alMaxProperty) : Boolean = {
        implicit val t = Timeout(0.5 second)
        val f = calc_router.single.get map (x => x ? can_sign_job())
        p.subs.length <= (f.map (x => Await.result(x, 0.5 seconds)).count(x => x.isInstanceOf[sign_job_can_accept]))
    }

    def signJob(p : alMaxProperty) = {
        // TODO: 需要添加算能管理
        // TODO: 多机器分配
        calc_router.single.get.head ! group_job(p)

        atomic { implicit tnx =>
            waiting_jobs() = waiting_jobs().tail
            calcing_jobs() = calcing_jobs() :+ p
        }
    }
}

