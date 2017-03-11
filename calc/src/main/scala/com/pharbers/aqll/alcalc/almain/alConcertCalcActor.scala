package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.aljobs.alJob.concert_calculation_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{concert_adjust, concert_adjust_result, concert_calc}

import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData

/**
  * Created by Alfred on 11/03/2017.
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
            val cj = concert_calculation_jobs(Map(concert_calculation_jobs.max_uuid -> p.uuid, concert_calculation_jobs.calc_uuid -> p.subs(index.single.get).uuid))
            val result = cj.result
            println(cj.cur.get.storages.head.asInstanceOf[alStorage].data.head.getClass)
            println(cj.cur.get.storages.head.asInstanceOf[alStorage].data.head.asInstanceOf[IntegratedData].getHospName)
        }
    }
}
