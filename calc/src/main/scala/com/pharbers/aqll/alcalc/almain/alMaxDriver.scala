package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, ActorSystem, Props, Scheduler}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.{RoundRobinPool}
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alcalc.aljobs.alJob
import com.pharbers.aqll.alcalc.aljobs.alJob.max_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger._
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{spliting_busy, _}
import com.pharbers.aqll.alcalc.almaxdefines.alMaxProperty
import com.pharbers.aqll.calc.split.{SplitAggregator, SplitGroupMaster}

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
            // TODO: 需要添加算能管理
//            println(s"${self.path} current time table : ${calcing_jobs.single.get}")
            atomic { implicit txn =>
                waiting_jobs() match {
                    case head :: lst => {
                        val f = calc_router ? clac_job(head)
                        Await.result(f, 0.5 seconds) match {
                            case clacing_busy() => Unit
                            case clacing_accept() => {
                                waiting_jobs() = waiting_jobs().tail
                                calcing_jobs() = calcing_jobs() :+ head
                            }
                        }
                    }
                    case Nil => Unit
                }
            }
        }

        case _ => ???
    }

    val excel_split_router = CreateExcelSplitRouter
    val calc_router = alCreateCalcRouter
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

trait alCreateCalcRouter { this : Actor =>
//    def alCreateCalcRouter =
//        context.actorOf(RoundRobinPool(1).props(alCalcActor.props), name = "calc-router")

    // TODO : 由于是有状态的需要改成注册机制, 不能用router
    def alCreateCalcRouter =
        context.actorOf(
            ClusterRouterPool(RoundRobinPool(1), ClusterRouterPoolSettings(
                totalInstances = 1, maxInstancesPerNode = 1,
                allowLocalRoutees = true, useRole = None)).props(alCalcActor.props), name = "calc-router")
}

