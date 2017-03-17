package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, FSM, Props}
import com.pharbers.aqll.alcalc.aljobs.aljobstates.{alMasterJobIdle, alPointState}
import com.pharbers.aqll.alcalc.aljobs.aljobstates.alExcelSplitJobStates._
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.calc.util.GetProperties

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by Alfred on 11/03/2017.
  */
object alExcelSplitActor {
    def props : Props = Props[alExcelSplitActor]
}

class alExcelSplitActor extends Actor
                        with ActorLogging
                        with FSM[alPointState, String] {

    startWith(alMasterJobIdle, "")

    when(alMasterJobIdle) {
        case Event(split_job(s), _) => {
            sender() ! spliting_job(s)
            context.system.scheduler.scheduleOnce(0 seconds, self, spliting_job(s))
            goto(spliting_data) using ""
        }
    }

    when(spliting_data) {
        case Event(split_job(s), _) => {
            sender() ! spliting_busy()
            stay()
        }

        case Event(spliting_job(s), _) => {
            val result = s.result
                //"akka.tcp://calc@127.0.0.1:2551/user/splitreception"
            val singleton = context.actorSelection(GetProperties.singletonPaht)
                //context.actorSelection("akka://calc/user/splitreception")
            val (p, sb) = result.map (x => x).getOrElse(throw new Exception("cal error"))
            singleton ! finish_split_excel_job(p.toString, sb.asInstanceOf[List[String]])
            goto(alMasterJobIdle) using ""
        }
    }
}
