package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.aljobs.alJob.concert_grouping_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines.presist_data
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData

import scala.concurrent.stm.{Ref, atomic}

/**
  * Created by Alfred on 13/03/2017.
  */
object alConcertCalcActor {
    def props : Props = Props[alConcertCalcActor]
}

class alConcertCalcActor extends Actor
                            with ActorLogging {

    val index = Ref(-1)

    override def receive = {
        case concert_adjust() => sender() ! concert_adjust_result(-1)
        case concert_adjust_result(i) => atomic { implicit tnx =>
            index() = i
        }
        case concert_calc(p) => {
            println(p)
        }
        case _ => ???
    }
}