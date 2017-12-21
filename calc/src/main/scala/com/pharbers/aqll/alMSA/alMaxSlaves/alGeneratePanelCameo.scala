package com.pharbers.aqll.alMSA.alMaxSlaves

import scala.concurrent.duration._
import play.api.libs.json.JsString
import scala.collection.immutable.Map
import play.api.libs.json.Json.toJson
import com.pharbers.panel.pfizer.phPfizerHandle
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alCalcHelp.alWebSocket.alWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.panelMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg._
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.ymMsg.calcYM_end

/**
  * Created by jeorch on 17-10-11.
  *     Modify by clock on 2017.12.19
  */
object alGeneratePanelCameo {
    def props(panelJob: alPanelItem,
              counter: ActorRef) = Props(new alGeneratePanelCameo(panelJob, counter))
}

class alGeneratePanelCameo(panelJob: alPanelItem, counter: ActorRef) extends Actor with ActorLogging {
    //TODO shijian chuancan
    val timeoutMessager = context.system.scheduler.scheduleOnce(30 minute) {
        self ! generate_panel_timeout()
    }

    override def postRestart(reason: Throwable) = {
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {
        case generate_panel_start_impl(_) => {
            val args: Map[String, List[String]] = Map(
                "company" -> List(panelJob.company),
                "uid" -> List(panelJob.uid),
                "cpas" -> panelJob.cpa.split("&").toList,
                "gycxs" -> panelJob.gycx.split("&").toList
            )
            alTempLog(s"开始生成${panelJob.ym}月份的panel, args=" + args)

            val (result, panelResult) = try {
                val panelResult = phPfizerHandle(args).getPanelFile(panelJob.ym)
                alTempLog(s"generate panel result = $panelResult")
                (true, panelResult)
            } catch {
                case ex: Exception =>
                    alTempLog("Warning! cannot generate panel" + ex.getMessage)
                    (false, toJson("cannot generate panel"))
            }

            self ! generate_panel_end(true, panelResult)
        }

        case generate_panel_end(result, panelResult) => {
            result match {
                case true => alTempLog("generate panel => Success")
                case false =>
                    val msg = Map(
                        "type" -> "error",
                        "error" -> "cannot generate panel"
                    )
                    alWebSocket(panelJob.uid).post(msg)
                    alTempLog("generate panel => Failed")
            }
            shutSlaveCameo(generatePanelResult(panelJob.uid, panelResult))
        }

        case generate_panel_timeout() => {
            log.info("Warning! generate panel timeout")
            alTempLog("Warning! generate panel timeout")
            self ! generate_panel_end(false, toJson(" "))
        }

        case canDoRestart(reason: Throwable) =>
            super.postRestart(reason)
            alTempLog("Warning! generate_panel Node canDoRestart")
            self ! generate_panel_start_impl(panelJob)

        case cannotRestart(reason: Throwable) => {
            log.info(s"Warning! generate_panel Node reason is ${reason}")
            alTempLog("Warning! generate_panel Node cannotRestart, reason is $reason")
            self ! generate_panel_end(false, toJson(" "))
        }

        case msg : AnyRef => alTempLog(s"Warning! Message not delivered. alGeneratePanelCameo.received_msg=$msg")
    }

    def shutSlaveCameo(msg: AnyRef) = {
        timeoutMessager.cancel()

        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! msg

        log.info("stop generate_panel cameo")
        alTempLog("stop generate_panel cameo")

        self ! PoisonPill
    }
}
