package com.pharbers.aqll.alMSA.alCalcMaster

import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.ymMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.generatePanel._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.splitPanel._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_result
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoRestoreBson.restore_bson_end

/**
  * Created by clock on 17-11-22.
  */
object alMaxMaster {
    def props = Props[alMaxMaster]
    def name = "driver-actor"

    //group module
    case class pushGroupJob(item: alMaxRunning)
    case class groupSchedule()
    case class groupPanelResult(item: alMaxRunning)

    //scp module
    case class pushScpJob(item: alMaxRunning)
    case class scpSchedule()
    case class scpResult(item: alMaxRunning)

    //calc module
    case class pushCalcJob(item: alMaxRunning)
    case class sumCalcJob(items: alMaxRunning, s: ActorRef)
    case class calcSchedule()

}

/**
  * Created by clock on 2017.12.18
  */
class alMaxMaster extends Actor with ActorLogging with alMaxMasterTrait {
    import alMaxMaster._
    override def receive = {
        case startCalcYm(item) => self ! pushCalcYMJob(item)
        case startGeneratePanel(item) => self ! pushGeneratePanelJob(item)
        case startCalc(uid) => self ! pushSplitPanel(uid)

        //calc ym module
        case pushCalcYMJob(item) => preCalcYMJob(item)
        case calcYMSchedule() => calcYMScheduleJobs
        case calcYMResult(ym, mkt) => postCalcYMJob(ym, mkt)

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
        case groupSchedule() => schduleGroupJob
        case groupPanelResult(item) => postGroupJob(item)

        //scp module
        case pushScpJob(item) => preScpJob(item)
        case scpSchedule() => schduleScpJobs
        case scpResult(item) => releaseScpEnergy
            postScpJob(item)

        //calc module
        case pushCalcJob(item) => preCalcJob(item)
        case sumCalcJob(items, s) => doSum(items, s)
        case calcSchedule() => schduleCalcJob
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
