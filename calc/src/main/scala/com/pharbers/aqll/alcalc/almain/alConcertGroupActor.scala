package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.aljobs.alJob.concert_grouping_jobs
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger._

import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData

import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.alcalc.alprecess.alprecessdefines.alPrecessDefines.presist_data

/**
  * Created by Alfred on 11/03/2017.
  */
object alConcertGroupActor {
    def props : Props = Props[alConcertGroupActor]
}

class alConcertGroupActor extends Actor
                            with ActorLogging {

    val index = Ref(-1)

    override def receive = {
        case concert_adjust() => sender() ! concert_adjust_result(-1)
        case concert_adjust_result(i) => atomic { implicit tnx =>
            index() = i
        }
        case concert_group(p) => {
            val cj = concert_grouping_jobs(Map(concert_grouping_jobs.max_uuid -> p.uuid, concert_grouping_jobs.group_uuid -> p.subs(index.single.get).uuid))
            cj.result
            val concert = cj.cur.get.storages.head.asInstanceOf[alStorage]
            val m = alStorage.groupBy (x =>
                    (x.asInstanceOf[IntegratedData].getYearAndmonth, x.asInstanceOf[IntegratedData].getMinimumUnitCh)
                )(concert)

            val g = alStorage(m.values.map (x => x.asInstanceOf[alStorage].data.head.toString).toList)
            g.doCalc
            val sg = alStage(g :: Nil)
            val pp = presist_data(Some(p.subs(index.single.get).uuid), Some("group"))
            pp.precess(sg)
            sender() ! concert_group_result(p.subs(index.single.get).uuid)
        }
    }
}
