package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.{split_excel_hand, split_excel_start_impl}

/**
  * Created by alfredyang on 12/07/2017.
  */

object alSplitExcelSlave {
    def props = Props[alSplitExcelSlave]
    def name = "split-excel-slave"
}

class alSplitExcelSlave extends Actor with ActorLogging {
    override def receive: Receive = {
        case split_excel_hand() => sender ! split_excel_hand()
        case split_excel_start_impl(file, parmary) => {
            val cur = context.actorOf(alSplitExcelComeo.props(file, parmary, sender, self))
            context.watch(cur)
            cur.tell(split_excel_start_impl(file, parmary), sender)
        }
    }}
