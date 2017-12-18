package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.ActorRef
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem

/**
  * Created by clock on 17-12-18.
  */
object alCalcMsg {
    //calc ym module
    object ymMsg {
        case class pushCalcYMJob(item: alPanelItem)
        case class calcYMSchedule()
        case class calcYMResult(ym: String)

        case class calcYM_start()
        case class calcYM_hand()
        case class calcYM_start_impl(panel_job: alPanelItem)
        case class calcYM_end2(result: Boolean, ym: String, mkts: String)
        case class calcYM_end(result: Boolean, ym: String)
        case class calcYM_timeout()
    }

    object generPanel {

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
