package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, ActorSystem, Props, Scheduler}
import akka.routing.RoundRobinPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alcalc.aljobs.alJob
import com.pharbers.aqll.alcalc.aljobs.alJob.max_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger._
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{spliting_busy, _}

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
                     with alCreateExcelSplitRouter {

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
            implicit val t = Timeout(0.5 second)
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

        case _ => ???
    }

    val excel_split_router = CreateExcelSplitRouter
}

trait alMaxJobsSchedule { this : Actor =>
    val jobs = Ref(List[alJob]())
    val timer = context.system.scheduler.schedule(0 seconds, 1 seconds, self, new schedule_jobs)
}

trait alCreateExcelSplitRouter { this : Actor =>
    def CreateExcelSplitRouter =
        context.actorOf(RoundRobinPool(1).props(alExcelSplitActor.props), name = "excel-split-router")
}