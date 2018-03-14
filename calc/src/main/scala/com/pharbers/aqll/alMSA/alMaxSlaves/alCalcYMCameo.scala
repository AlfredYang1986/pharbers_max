package com.pharbers.aqll.alMSA.alMaxSlaves

import play.api.libs.json._

import scala.concurrent.duration._
import scala.collection.immutable.Map
import com.pharbers.panel.phPanelHeadle
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem

import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.ymMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg._
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alCalcHelp.alWebSocket.phWebSocket

/**
  * Created by jeorch on 17-10-11.
  *     Modify by clock on 2017.12.19
  */
object alCalcYMCameo {
    def props(calcYM_job: alPanelItem,
              counter: ActorRef) = Props(new alCalcYMCameo(calcYM_job, counter))
}

class alCalcYMCameo(calcYMJob: alPanelItem, counter: ActorRef) extends Actor with ActorLogging {
    //TODO shijian chuancan
    val timeoutMessager = context.system.scheduler.scheduleOnce(30 minute) {
        self ! calcYM_timeout()
    }

    override def postRestart(reason: Throwable) = {
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {
        case calcYM_start_impl(_) => {
            val args: Map[String, List[String]] = Map(
                "company" -> List(calcYMJob.company),
                "uid" -> List(calcYMJob.uid),
                "cpas" -> calcYMJob.cpa.split("&").toList,
                "gycxs" -> calcYMJob.gycx.split("&").toList
            )
            alTempLog("开始过滤日期,arg=" + args)

            val (result, ym, mkt) = try {
                val header = phPanelHeadle(args)
                val ym = header.calcYM.asInstanceOf[JsString].value
                val markets = header.getMarkets.asInstanceOf[JsString].value
                alTempLog(s"calcYM result, ym = $ym, mkt = $markets")
                (true, ym, markets)
            } catch {
                case ex: Exception =>
                    alTempLog("Warning! cannot calcYM" + ex.getMessage)
                    (false, "0"," ")
            }

            self ! calcYM_end(result, ym, mkt)
        }

        case calcYM_end(result, ym, mkt) => {
            result match {
               case true => alTempLog("calc ym => Success")
               case false =>
                   val msg = Map(
                       "type" -> "error",
                       "error" -> "cannot calc ym"
                   )
                   phWebSocket(calcYMJob.uid).post(msg)
                   alTempLog("calc ym => Failed")
            }
            shutSlaveCameo(calcYMResult(calcYMJob.uid, ym, mkt))
        }

        case calcYM_timeout() => {
            log.info("Warning! calc ym timeout")
            alTempLog("Warning! calc ym timeout")
            self ! calcYM_end(false, "0", " ")
        }

        case canDoRestart(reason: Throwable) => {
            super.postRestart(reason)
            alTempLog("Warning! calc_ym Node canDoRestart")
            self ! calcYM_start_impl(calcYMJob)
        }

        case cannotRestart(reason: Throwable) => {
            log.info(s"Warning! calc_ym Node reason is $reason")
            alTempLog(s"Warning! calc_ym Node cannotRestart, reason is $reason")
            self ! calcYM_end(false, "0", " ")
        }

        case msg: AnyRef => alTempLog(s"Warning! Message not delivered. alCalcYMCameo.received_msg=$msg")
    }

    def shutSlaveCameo(msg: AnyRef) = {
        timeoutMessager.cancel()

        val agent = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
        agent ! msg

        log.info("stop calcYM cameo")
        alTempLog("stop calcYM cameo")

        self ! PoisonPill
    }
}
