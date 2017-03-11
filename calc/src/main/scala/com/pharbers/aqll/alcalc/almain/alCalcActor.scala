package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alcalc.aljobs.alJob.calculation_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobstates.alMaxCalcJobStates.{calc_coreing, calc_doing}
import com.pharbers.aqll.alcalc.aljobs.aljobstates.{alMasterJobIdle, alPointState}
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alcalc.almaxdefines.alMaxProperty

import scala.concurrent.stm.atomic
import scala.concurrent.stm.Ref
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by BM on 11/03/2017.
  */
object alCalcActor {
    def props : Props = Props[alCalcActor]
}

class alCalcActor extends Actor
                     with ActorLogging
                     with FSM[alPointState, String]
                     with alCreateConcretCalcRouter {

    startWith(alMasterJobIdle, "")

    when(alMasterJobIdle) {
        case Event(calc_can_job(), _) => {
            sender() ! calcing_can_accept()
            stay()
        }

        case Event(calc_job(p), _) => {
            atomic { implicit tnx =>
                concert_ref() = Some(p)
            }
//            sender() ! calcing_accept()

            println(Map(calculation_jobs.max_uuid -> p.uuid, calculation_jobs.calc_uuid -> p.subs.head.uuid))
            val cj = calculation_jobs(Map(calculation_jobs.max_uuid -> p.uuid, calculation_jobs.calc_uuid -> p.subs.head.uuid))
            context.system.scheduler.scheduleOnce(0 seconds, self, calcing_job(cj))
            goto(calc_coreing) using ""
        }
    }

    when(calc_coreing) {
        case Event(calcing_job(cj), _) => {
            println(s"开始根据CPU核数拆分线程")
            println(cj)

            val result = cj.result
            val (p, sb) = result.get.asInstanceOf[(String, List[String])]

            val q = sb.map (x => alMaxProperty(p, x, Nil))
            atomic { implicit tnx =>
                result_ref() = Some(alMaxProperty(concert_ref.single.get.get.uuid, p, q))
                adjust_index() = -1
            }
            println(result_ref.single.get)

            concert_router ! concert_adjust()

            goto(calc_doing) using ""
        }
    }

    when(calc_doing) {
        case Event(calc_can_job(), _) => {
            sender() ! calcing_busy()
            stay()
        }

        case Event(calc_job(p), _) => {
            sender() ! calcing_busy()
            stay()
        }
    }

    whenUnhandled {
        case Event(calc_can_job(), _) => {
            sender() ! calcing_busy()
            stay()
        }

        case Event(calc_job(p), _) => {
            sender() ! calcing_busy()
            stay()
        }

        case Event(concert_adjust_result(_), _) => {
            atomic { implicit tnx =>
                adjust_index() = adjust_index() + 1
                sender() ! concert_adjust_result(adjust_index())
            }

            if (adjust_index.single.get == 3) {
                concert_router ! concert_calc(result_ref.single.get.get)
            }
            stay()
        }
    }

    val concert_ref : Ref[Option[alMaxProperty]] = Ref(None)
    val result_ref : Ref[Option[alMaxProperty]] = Ref(None)
    val adjust_index = Ref(-1)
    val concert_router = CreateConcretCalcRouter
}

trait alCreateConcretCalcRouter { this : Actor =>
    def CreateConcretCalcRouter =
        context.actorOf(BroadcastPool(4).props(alConcertCalcActor.props), name = "concret-router")
}