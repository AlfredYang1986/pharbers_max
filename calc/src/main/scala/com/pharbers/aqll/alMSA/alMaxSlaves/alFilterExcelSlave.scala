package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.{filter_excel_hand, filter_excel_start_impl}

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
            val cur = context.actorOf(alFilterExcelComeo.props(file, parmary, sender, self))
            context.watch(cur)
            cur.tell(filter_excel_start_impl(file, parmary), sender)
        }
    }
}




