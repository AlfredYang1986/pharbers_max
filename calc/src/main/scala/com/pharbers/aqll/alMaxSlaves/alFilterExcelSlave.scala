package com.pharbers.aqll.alMaxSlaves

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMaster.alMasterTrait.alCameoFilterExcel
import com.pharbers.aqll.alCalcMaster.alMasterTrait.alCameoFilterExcel.{filter_excel_end, filter_excel_hand, filter_excel_start, filter_excel_start_impl}
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_filter_excel_jobs
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.push_max_job
import com.pharbers.aqll.common.alString.alStringOpt.removeSpace

/**
  * Created by alfredyang on 11/07/2017.
  */

object alFilterExcelSlave {
    def props = Props[alFilterExcelSlave]
    def name = "filter-excel-slave"
}

class alFilterExcelSlave extends Actor with ActorLogging {
    override def receive: Receive = {
        case filter_excel_hand() => sender ! filter_excel_hand()
        case filter_excel_start_impl(file, parmary) => {
            val cur = context.actorOf(alFilterExcelComeo.props(sender, self))
            context.watch(cur)
            cur.tell(filter_excel_start_impl(file, parmary), sender)
        }
    }
}




