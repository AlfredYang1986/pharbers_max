package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, FSM, Props}
import com.pharbers.aqll.alcalc.aljobs.alJob.calculation_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobstates.alMaxCalcJobStates.calc_coreing
import com.pharbers.aqll.alcalc.aljobs.aljobstates.{alMasterJobIdle, alPointState}
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{clac_job, clacing_accept, clacing_busy, clacing_job}
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
        case Event(clac_job(p), _) => {
            atomic { implicit tnx =>
                result_ref() = Some(p)
            }
            sender() ! clacing_accept()

            println(Map(calculation_jobs.max_uuid -> p.uuid, calculation_jobs.calc_uuid -> p.subs.head.uuid))
            calculation_jobs(Map(calculation_jobs.max_uuid -> p.uuid, calculation_jobs.calc_uuid -> p.subs.head.uuid))
            context.system.scheduler.scheduleOnce(0 seconds, self, clacing_job(null))
            goto(calc_coreing) using ""
        }
    }

    when(calc_coreing) {
        case Event(clac_job(p), _) => {
            sender() ! clacing_busy()
            stay()
        }

        case Event(clacing_job(j), _) => {
            stay()
        }
    }

    val result_ref : Ref[Option[alMaxProperty]] = Ref(None)
}
