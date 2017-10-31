package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait._
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_slave_status

/**
  * Created by alfredyang on 11/s07/2017.
  */
object alMaxMaster {
    def props = Props[alMaxMaster]
    def name = "driver-actor"
}

class alMaxMaster extends Actor
                    with ActorLogging
                    with alFilterExcelTrait
                    with alSplitExcelTrait
                    with alGroupDataTrait
                    with alRestoreBsonTrait
                    with alCalcDataTrait {

    override def receive: Receive = {
        case filter_excel_job_2(file, parmary) => pushFilterJob(file, parmary, sender)
        case filter_excel_schedule() => schduleFilterJob

        case push_split_excel_job(file, parmary) => pushSplitExcelJob(file, parmary, sender)
        case split_excel_schedule() => schduleSplitExcelJob

        case push_group_job(property) => pushGroupJob(property, sender)
        case group_schedule() => schduleGroupJob

        case push_calc_job_2(property, parmary) => pushCalcJob(property, parmary, sender)
        case calc_schedule() => schduleCalcJob
        case calc_slave_status() => Unit // setSlaveStatus

        case push_restore_job(coll, sub_uuid) => pushRestoreJob(coll, sub_uuid, sender)
        case restore_bson_schedule() => schduleRestoreJob
    }

}
