package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.{alCalcDataTrait, alFilterExcelTrait, alGroupDataTrait, alSplitExcelTrait}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcAgent.alSingleAgentMaster
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.filter_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end

/**
  * Created by alfredyang on 11/s07/2017.
  */
object alMaxMaster {
    def props(act: ActorRef) = Props(new alMaxMaster(act))
    def name = "driver-actor"
}

class alMaxMaster(act: ActorRef) extends Actor
                    with ActorLogging
                    with alFilterExcelTrait
                    with alSplitExcelTrait
                    with alGroupDataTrait
                    with alCalcDataTrait {

    override def receive: Receive = {
        case filter_excel_job_2(file, parmary) => pushFilterJob(file, parmary, sender)
        case filter_excel_schedule() => schduleJob(act)

        case push_split_excel_job(file, parmary) => pushSplitExcelJob(file, parmary, sender)
        case split_excel_schedule() => schduleSplitExcelJob(act)

        case push_group_job(property) => pushGroupJob(property, sender)
        case group_schedule() => schduleGroupJob(act)

        case push_calc_job_2(property, parmary) => pushCalcJob(property, parmary, sender)
        case calc_schedule() => schduleCalcJob(act)

//        case msg: filter_excel_end => println(s"## Master-Process 终焉 => ${msg}##")
//        case msg: split_excel_end => println(s"## Master-Process 终焉 => ${msg}##")
//        case msg: group_data_end => println(s"## Master-Process 终焉 => ${msg}##")
//        case msg: calc_data_end => println(s"## Master-Process 终焉 => ${msg}##")

    }
}
