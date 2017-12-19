package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.ActorRef
import play.api.libs.json.JsValue
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning

/**
  * Created by clock on 17-12-18.
  */
object alCalcMsg {
    case class startCalcYm(item: alPanelItem)
    case class startGeneratePanel(item: alPanelItem)
    case class startCalc(uid: String)

    //calc ym module
    object ymMsg {
        case class pushCalcYMJob(item: alPanelItem)
        case class calcYMSchedule()
        case class calcYMResult(ym: List[String], mkt: List[String])

        case class calcYM_start()
        case class calcYM_hand()
        case class calcYM_start_impl(panel_job: alPanelItem)
        case class calcYM_end(result: Boolean, ym: String, mkt: String)
        case class calcYM_timeout()
    }

    //generate panel module
    object generatePanel {
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
    object splitPanel {
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
    object group {
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
}
