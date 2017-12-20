package com.pharbers.aqll.alMSA.alCalcMaster

import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.ymMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.panelMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.splitPanelMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.groupMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.scpMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg._

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoRestoreBson.restore_bson_end

/**
  * Created by clock on 2017.12.18
  *     Modify by clock on 2017.12.20
  */
object alMaxMaster {
    def props = Props[alMaxMaster]
    def name = "driver-actor"
}

class alMaxMaster extends Actor with ActorLogging with alMaxMasterTrait {
    override def receive = {
        case startCalcYm(item) => self ! pushCalcYMJob(item)
        case startGeneratePanel(item) => self ! pushGeneratePanelJob(item)
        case startCalc(uid) => self ! pushSplitPanel(uid)

        //calc ym module
        case pushCalcYMJob(item) => preCalcYMJob(item)
        case calcYMSchedule() => calcYMScheduleJobs
        case calcYMResult(uid, ym, mkt) => postCalcYMJob(uid, ym, mkt)

        //generate panel module
        case pushGeneratePanelJob(item) => preGeneratePanelJob(item)
        case generatePanelSchedule() => generatePanelScheduleJobs
        case generatePanelResult(uid, panelResult) => postGeneratePanelJob(uid, panelResult)

        //split panel file module
        case pushSplitPanel(uid) => preSplitPanelJob(uid)
        case splitPanelSchedule() => splitPanelSchduleJobs
        case splitPanelResult(item, parent, subs) => postSplitPanelJob(item, parent, subs)

        //group splited file module
        case pushGroupJob(item) => preGroupJob(item)
        case groupSchedule() => groupScheduleJobs
        case groupPanelResult(item) => postGroupJob(item)

        //scp module
        case pushScpJob(item) => preScpJob(item)
        case scpSchedule() => scpSchduleJobs
        case scpResult(item) => postScpJob(item)

        //calc module
        case pushCalcJob(item) => preCalcJob(item)
        case sumCalcJob(items, s) => doSum(items, s)
        case calcSchedule() => calcScheduleJobs
        case calc_data_result(uid, tid, v, u, result) => postCalcJob(uid, tid, v, u, result)

        //restore module
        case push_restore_job(uid) => preRestoreJob(uid, sender)
        case restore_bson_schedule() => schduleRestoreJob
        case restore_bson_end(bool, uid) => postRestoreJob(bool, uid)

        //Energy Manage
        case refundNodeSuccess() => Unit

        case msg: AnyRef => alTempLog("alMaxMaster not match msg = " + msg)
    }
}
