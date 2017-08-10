package com.pharbers.aqll.alMSA.alCalcAgent

import akka.actor.{Actor, ActorLogging, ActorSelection, PoisonPill, Props}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.filter_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end

/**
  * Created by jeorch on 17-8-9.
  */

object alSingleAgentMasterComeo {
    def props = Props[alSingleAgentMasterComeo]
}

class alSingleAgentMasterComeo extends Actor with ActorLogging{

    val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")

    override def receive : Receive = {
        case msg: filter_excel_job_2 => a ! msg
        case msg: push_split_excel_job => a ! msg
        case msg: push_group_job => a ! msg
        case msg: push_calc_job_2 => a ! msg

//        case msg: filter_excel_end => println(s" ## 模拟的临时发送者-终焉 => ${msg}")
//        case msg: split_excel_end => println(s" ## 模拟的临时发送者-终焉 => ${msg}")
//        case msg: group_data_end => println(s" ## 模拟的临时发送者-终焉 => ${msg}")
//        case msg: calc_data_end => println(s" ## 模拟的临时发送者-终焉 => ${msg}")

    }
}
