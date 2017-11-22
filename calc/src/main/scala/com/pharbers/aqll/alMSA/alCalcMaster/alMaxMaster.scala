package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_slave_status
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem

/**
  * Created by clock on 17-11-22.
  */
object alMaxMaster {
    def props = Props[alMaxMaster]
    def name = "driver-actor"

    //calc ym module
    case class pushCalcYMJobs(item : alPanelItem)
    case class calcYMSchedule()
    case class releaseCalcYMEnergy()
    case class calcYMResult(ym: String)

    //generate panel module
    case class pushGeneratePanelJobs(item : alPanelItem)
    case class generatePanelSchedule()
    case class generatePanelResult(paths: String)

    //filter module
    case class pushFilterJob(item : alPanelItem)
    case class generatePanelSchedule2()
    case class generatePanelResult2(paths: String)

    //scp module
    case class PushToScpQueue(file: String, target: String, host: String, user: String)
    case class scpSchedule()
}

/**
  * Created by alfredyang on 11/s07/2017.
  */
class alMaxMaster extends Actor with ActorLogging with alMaxMasterTrait {
    import alMaxMaster._
    override def receive: Receive = {
        //calc ym module
        case pushCalcYMJobs(item) => preCalcYMJobs(item, sender)
        case calcYMSchedule() => calcYMScheduleJobs
        case releaseCalcYMEnergy() => releaseCalcYMEnergy
        case calcYMResult(ym) => println(s"calcYM = ${ym}")

        //generate panel module
        case pushGeneratePanelJobs(item) => preGeneratePanelJobs(item, sender)
        case generatePanelSchedule() => generatePanelScheduleJobs
        case generatePanelResult(panelLst) => println(s"panelLst = ${panelLst}")

        //filter panel module
        case filter_excel_job_2(file, parmary) => pushFilterJob(file, parmary, sender)
        case filter_excel_schedule() => schduleFilterJob

        case push_split_excel_job(file, parmary) => pushSplitExcelJob(file, parmary, sender)
        case split_excel_schedule() => schduleSplitExcelJob

        case push_group_job(property) => pushGroupJob(property, sender)
        case group_schedule() => schduleGroupJob

        case push_calc_job_2(property, parmary) => pushCalcJob(property, parmary, sender)
        case calc_schedule() => schduleCalcJob
        case calc_slave_status() => Unit // setSlaveStatus

        case push_restore_job(coll, sub_uuids) => pushRestoreJob(coll, sub_uuids, sender)
        case restore_bson_schedule() => schduleRestoreJob

        //scp module
        case scpSchedule() => scanQueue()
    }

}
