package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, FSM, Props}
import com.pharbers.aqll.alcalc.aljobs.alJob.calculation_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobstates.alMaxCalcJobStates.calc_coreing
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
                     with FSM[alPointState, String] {

    startWith(alMasterJobIdle, "")

    when(alMasterJobIdle) {
        case Event(calc_can_job(), _) => {
            sender() ! calcing_can_accept()
            stay()
        }

        case Event(calc_job(p), _) => {
            atomic { implicit tnx =>
                result_ref() = Some(p)
            }
//            sender() ! calcing_accept()

            println(Map(calculation_jobs.max_uuid -> p.uuid, calculation_jobs.calc_uuid -> p.subs.head.uuid))
            val cj = calculation_jobs(Map(calculation_jobs.max_uuid -> p.uuid, calculation_jobs.calc_uuid -> p.subs.head.uuid))
            context.system.scheduler.scheduleOnce(0 seconds, self, calcing_job(cj))
            goto(calc_coreing) using ""
        }
    }

    when(calc_coreing) {
        case Event(calc_can_job(), _) => {
            sender() ! calcing_busy()
            stay()
        }

        case Event(calc_job(p), _) => {
            sender() ! calcing_busy()
            stay()
        }

        case Event(calcing_job(cj), _) => {
            println(s"开始根据CPU核数拆分线程")
            println(cj)

            val result = cj.result
            println(result)

            stay()
        }
    }

    val result_ref : Ref[Option[alMaxProperty]] = Ref(None)
}
