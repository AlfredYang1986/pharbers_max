package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcStep, alMaxRunning}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_slave_status
import com.pharbers.aqll.alStart.alEntry.alActorTest.csv_panel
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import play.api.libs.json.JsValue

/**
  * Created by clock on 17-11-22.
  */
object alMaxMaster {
    def props = Props[alMaxMaster]
    def name = "driver-actor"

    //calc ym module
    case class pushCalcYMJob(item: alPanelItem)
    case class calcYMSchedule()
    case class releaseCalcYMEnergy()
    case class calcYMResult(ym: String)

    //generate panel module
    case class pushGeneratePanelJob(item: alPanelItem)
    case class generatePanelSchedule()
    case class generatePanelResult(uid: String, panelResult: JsValue)

    //split panel module
    case class pushSplitPanelJob(uid: String)
    case class splitPanelSchedule()

    //group module
    case class pushGroupJob(item: alMaxRunning)
    case class groupSchedule()

    //calc module
    case class pushCalcJob(item: alMaxRunning)
    case class calcSchedule()

    //scp module
    case class pushToScpQueue(file: String, target: String, host: String, user: String)
    case class scpSchedule()
}

/**
  * Created by alfredyang on 11/s07/2017.
  */
class alMaxMaster extends Actor with ActorLogging with alMaxMasterTrait {
    import alMaxMaster._
    override def receive: Receive = {
        //calc ym module
        case pushCalcYMJob(item) => preCalcYMJob(item, sender)
        case calcYMSchedule() => calcYMScheduleJobs
        case releaseCalcYMEnergy() => releaseCalcYMEnergy
        case calcYMResult(ym) => println(s"calcYM = ${ym}")

        //generate panel module
        case pushGeneratePanelJob(item) => preGeneratePanelJob(item, sender)
        case generatePanelSchedule() => generatePanelScheduleJobs
        case generatePanelResult(uid, panelResult) => postGeneratePanelJob(uid, panelResult)

        //split panel file module
        case pushSplitPanelJob(uid) => preSplitPanelJob(uid, sender)
        case splitPanelSchedule() => schduleSplitPanelJob

        //group splited file module
        case push_group_job(property) => pushGroupJob(property, sender)
        case group_schedule() => schduleGroupJob

        //calc module
        case push_calc_job_2(property, parmary) => pushCalcJob(property, parmary, sender)
        case calc_schedule() => schduleCalcJob
        case calc_slave_status() => Unit // setSlaveStatus

        //restore module
        case push_restore_job(coll, sub_uuids) => pushRestoreJob(coll, sub_uuids, sender)
        case restore_bson_schedule() => schduleRestoreJob

        //scp module
        case scpSchedule() => scanQueue()
    }

}
