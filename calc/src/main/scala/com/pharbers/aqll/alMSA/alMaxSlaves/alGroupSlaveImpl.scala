package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.concert_grouping_jobs
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.concert_group_result
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines.presist_data
import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.{group_data_end, group_data_hand, group_data_start_impl}

/**
  * Created by alfredyang on 13/07/2017.
  */
object alGroupSlaveImpl {
    def props = Props[alGroupSlaveImpl]
}

class alGroupSlaveImpl extends Actor with ActorLogging {

    override def receive: Receive = {
        case group_data_hand() => sender ! group_data_hand()
        case group_data_start_impl(p) => {
            println(s"alfred grouping $p")
            val cj = concert_grouping_jobs(Map(concert_grouping_jobs.max_uuid -> p.parent, concert_grouping_jobs.group_uuid -> p.uuid))
            cj.result
            val concert = cj.cur.get.storages.head.asInstanceOf[alStorage]
            val m = alStorage.groupBy (x =>
                (x.asInstanceOf[IntegratedData].getYearAndmonth, x.asInstanceOf[IntegratedData].getMinimumUnitCh)
            )(concert)

            val g = alStorage(m.values.map (x => x.asInstanceOf[alStorage].data.head.toString).toList)
            g.doCalc
            val sg = alStage(g :: Nil)
            val pp = presist_data(Some(p.uuid), Some("group"))
            pp.precess(sg)
            p.grouped = true
            sender ! group_data_end(true, p)
        }
    }
}
