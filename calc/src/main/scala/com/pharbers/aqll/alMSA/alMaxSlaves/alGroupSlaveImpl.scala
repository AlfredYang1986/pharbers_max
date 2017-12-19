package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.alCalcMemory.aldata.alStorage
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.groupMsg._
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.concert_grouping_jobs
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.presist_data

/**
  * Created by alfredyang on 13/07/2017.
  *     Modify by clock on 2017.12.19
  */
object alGroupSlaveImpl {
    def props = Props[alGroupSlaveImpl]
}

class alGroupSlaveImpl extends Actor with ActorLogging {
    override def receive: Receive = {
        case group_data_hand() => sender ! group_data_hand()

        case group_data_start_impl(item) => {
            val cj = concert_grouping_jobs(
                Map(
                    concert_grouping_jobs.max_uuid -> item.parent,
                    concert_grouping_jobs.group_uuid -> item.tid
                )
            )
            cj.result
            val concert = cj.cur.get.storages.head.asInstanceOf[alStorage]
            val m = alStorage.groupBy (x =>
                (x.asInstanceOf[IntegratedData].getYearAndmonth, x.asInstanceOf[IntegratedData].getMinimumUnitCh)
            )(concert)

            val g = alStorage(m.values.map (x => x.data.head.toString).toList)
            g.doCalc
            val sg = alStage(g :: Nil)
            val pp = presist_data(Some(item.tid), Some("group"))
            pp.precess(sg)
            sender ! group_data_end(true, item)
        }
    }
}
