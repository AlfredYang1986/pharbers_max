package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.ActorRef
import play.api.libs.json.JsValue
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.aqll.alCalcHelp.alMaxDefines.alMaxRunning

/**
  * Created by clock on 17-12-18.
  */
object alCalcMsg {
    case class startCalcYm(item: alPanelItem)
    case class startGeneratePanel(item: alPanelItem)
    case class startCalc(uid: String)
    case class startAggregationCalcData(uid: String, showLst: List[String])
    case class startGenerateDeliveryFile(uid: String)

    //calc ym module
    object ymMsg {
        case class pushCalcYMJob(item: alPanelItem)
        case class calcYMSchedule()
        case class calcYMResult(uid: String, ym: String, mkt: String)

        case class calcYM_start()
        case class calcYM_hand()
        case class calcYM_start_impl(panel_job: alPanelItem)
        case class calcYM_end(result: Boolean, ym: String, mkt: String)
        case class calcYM_timeout()
    }

    //generate panel module
    object panelMsg {
        case class pushGeneratePanelJob(item: alPanelItem)
        case class generatePanelSchedule()
        case class generatePanelResult(uid: String, panelResult: JsValue)

        case class generate_panel_start()
        case class generate_panel_hand()
        case class generate_panel_start_impl(panel_job: alPanelItem)
        case class generate_panel_end(result: Boolean, panelResult: JsValue)
        case class generate_panel_timeout()
    }

    //split panel module
    object splitPanelMsg {
        case class pushSplitPanel(uid: String)
        case class splitPanelSchedule()
        case class splitPanelResult(item: alMaxRunning, parent: String, subs: List[String])

        case class split_panel_start()
        case class split_panel_hand()
        case class split_panel_start_impl(item: alMaxRunning)
        case class split_panel_end(result: Boolean, item: alMaxRunning, parent: String, subs: List[String])
        case class split_panel_timeout()
    }

    //group module
    object groupMsg {
        case class pushGroupJob(item: alMaxRunning)
        case class groupSchedule()
        case class groupPanelResult(item: alMaxRunning)

        case class group_data_start()
        case class group_data_hand()
        case class group_data_start_impl(item : alMaxRunning)
        case class group_data_end(result: Boolean, item : alMaxRunning)
        case class group_data_timeout()
    }

    //scp module
    object scpMsg {
        case class pushScpJob(item: alMaxRunning)
        case class scpSchedule()
        case class scpResult(item: alMaxRunning)

        case class scp_pkg()
        case class scp_unpkg()
        case class scp_timeout()

        case class pkgmsg(file: List[String], target: String)
        case class pkgmsgMuti(targets: List[Map[String, String]])
        case class scpmsg(file: String, target: String, host: String, user: String)
        case class scpmsgMutiPath(targets: List[Map[String, String]], host: String, user: String)
        case class unpkgmsg(target: String, des_dir: String, s: ActorRef)
        case class unpkgmsgMutiPath(target: List[String], des_dir: String, s: ActorRef)

        sealed class stop(t: Int, n: String)
        case class scpend(s: ActorRef) extends stop(0, "scp")
        case class pkgend(s: ActorRef) extends stop(1, "pkg")
        case class unpkgend(s: ActorRef) extends stop(2, "unpkg")
    }

    //calc data module
    object calcMsg {
        case class pushCalcJob(item: alMaxRunning)
        case class sumCalcJob(items: alMaxRunning, s: ActorRef)
        case class calcSchedule()
        case class calcDataResult(result: Boolean, uid: String, panel: String, v: Double, u: Double)

        case class calc_unpkg(tid: String, s: ActorRef)
        case class calc_data_start()
        case class calc_data_hand()
        case class calc_data_hand2(item: alMaxRunning)
        case class calc_data_start_impl(item: alMaxRunning)
        case class calc_data_start_impl3(sub_item: alMaxRunning, items: alMaxRunning)
        case class calc_data_sum()
        case class calc_data_average(item: alMaxRunning)
        case class calc_data_average_pre(avg_path: String)
        case class calc_data_average_one(avg_path: String, bsonpath: String)
        case class calc_data_average_post(item: alMaxRunning, panel: String, avg_path: String, bsonpath: String)
        case class calc_data_timeout()
    }

    // restore bson module
    object restoreMsg {
        case class pushRestoreJob(uid: String, panel: String)
        case class restoreBsonSchedule()
        case class restoreBsonResult(result: Boolean, uid: String)

        case class restore_bson_start()
        case class restore_bson_hand()
        case class restore_bson_start_impl(uid: String, panel: String)
        case class restore_bson_end(result: Boolean)
        case class restore_bson_timeout()
    }
    
    // aggregation data module
    object aggregationMsg {
        case class pushAggregationJob(uid: String, showLst: List[String])
        case class aggregationDataSchedule()
        case class aggregationDataResult(uid: String, table: String, result: Boolean)
  
        case class aggregationDataStart()
        case class aggregationDataHand()
        case class aggregationDataImpl(uid: String, company: String, temp: String)
        case class aggregationDataEnd(result: Boolean)
        case class aggregationDataTimeOut()
    }

    // generate delivery file
    object generateDeliveryFile {
        case class pushDeliveryJob(uid: String)
        case class generateDeliveryFileSchedule()
        case class generateDeliveryFileResult(uid: String, table: String, result: Boolean)

        case class generateDeliveryFileStart()
        case class generateDeliveryFileHand()
        case class generateDeliveryFileImpl(uid: String, company: String, temp: String)
        case class generateDeliveryFileEnd(fileName: String, result: Boolean)
        case class generateDeliveryFileTimeOut()
    }

    /**
      * for reStart count
      */
    object reStartMsg {
        case class canIReStart(reason: Throwable)
        case class canDoRestart(reason: Throwable)
        case class cannotRestart(reason: Throwable)
    }
}
