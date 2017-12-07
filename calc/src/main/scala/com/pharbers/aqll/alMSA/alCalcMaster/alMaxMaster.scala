package com.pharbers.aqll.alMSA.alCalcMaster

import play.api.libs.json.JsValue
import com.typesafe.config.ConfigFactory
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_result
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoRestoreBson.restore_bson_end

/**
  * Created by clock on 17-11-22.
  */
object alMaxMaster {
    val masterIP = ConfigFactory.load("split-new-master").getString("akka.remote.netty.tcp.hostname")
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
    case class pushSplitPanel(uid: String)
    case class splitPanelSchedule()
    case class splitPanelResult(item: alMaxRunning, parent: String, subs: List[String])

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
  * Created by alfredyang on 11/s07/2017.
  */
class alMaxMaster extends Actor with ActorLogging with alMaxMasterTrait {
    import alMaxMaster._
    override def receive = {
        //calc ym module
        case pushCalcYMJob(item) => preCalcYMJob(item)
        case calcYMSchedule() => calcYMScheduleJobs
        case releaseCalcYMEnergy() => releaseCalcYMEnergy
        case calcYMResult(ym) => println(s"calcYM = $ym")

        //generate panel module
        case pushGeneratePanelJob(item) => preGeneratePanelJob(item)
        case generatePanelSchedule() => generatePanelScheduleJobs
        case generatePanelResult(uid, panelResult) => postGeneratePanelJob(uid, panelResult)

        //split panel file module
        case pushSplitPanel(uid) => {
            println(s"master.pushSplitPanelJob(${uid})")
            preSplitPanelJob(uid)
        }
        case splitPanelSchedule() => schduleSplitPanelJob
        case splitPanelResult(item, parent, subs) => postSplitPanelJob(item, parent, subs)

        //group splited file module
        case pushGroupJob(item) => preGroupJob(item)
        case groupSchedule() => schduleGroupJob
        case groupPanelResult(item) => postGroupJob(item)

        //scp module
        case pushScpJob(item) => preScpJob(item)
        case scpSchedule() => schduleScpJobs
        case scpResult(item) => postScpJob(item)

        //calc module
        case pushCalcJob(item) => preCalcJob(item)
        case sumCalcJob(items, s) => doSum(items, s)
        case calcSchedule() => schduleCalcJob
        case calc_data_result(uid, tid, v, u, result) => postCalcJob(uid, tid, v, u, result)

        //restore module
        case push_restore_job(uid) => preRestoreJob(uid, sender)
        case restore_bson_schedule() => schduleRestoreJob
        case restore_bson_end(bool, uid) => postRestoreJob(bool, uid)

        case msg: AnyRef => log.info(s"Error Master msg=${msg}")
    }

}
