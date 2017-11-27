package com.pharbers.aqll.alMSA.alMaxSlaves

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{canDoRestart, canIReStart, cannotRestart}
import com.pharbers.aqll.alCalcOther.alMessgae.alWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGeneratePanel.{generate_panel_end, generate_panel_start_impl, generate_panel_timeout}
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.panel.pfizer.phPfizerHandle
import play.api.libs.json.Json.toJson
import scala.collection.immutable.Map

/**
  * Created by jeorch on 17-10-11.
  */
object alGeneratePanelCameo {
    def props(panel_job : alPanelItem,
              comeoActor : ActorRef,
              slaveActor : ActorRef,
              counter : ActorRef) = Props(new alGeneratePanelCameo(panel_job, comeoActor, slaveActor, counter))
}

class alGeneratePanelCameo(panel_job: alPanelItem,
                           comeoActor: ActorRef,
                           slaveActor: ActorRef,
                           counter: ActorRef) extends Actor with ActorLogging {

    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {
        case generate_panel_start_impl(panel_job) => {
            val args: Map[String, List[String]] = Map(
                "company" -> List(panel_job.company),
                "uid" -> List(panel_job.uid),
                "cpas" -> panel_job.cpa.split("&").toList,
                "gycxs" -> panel_job.gycx.split("&").toList
            )
            println(s"开始生成${panel_job.ym}月份的panel,args=" + args)
            val result = phPfizerHandle(args).getPanelFile(panel_job.ym)
            val msg = Map(
                "type" -> "generat_panel_result",
                "result" -> result.toString
            )
            println("generat panel result = " + msg)
            alWebSocket(panel_job.uid).post(msg)
            self ! generate_panel_end(panel_job.uid, result)
        }

        case generate_panel_end(uid, panelResult) => {
            slaveActor forward generate_panel_end(uid, panelResult)
            shutSlaveCameo(generate_panel_end(uid, panelResult))
        }

        case generate_panel_timeout() => {
            log.info("timeout occur")
            shutSlaveCameo(generate_panel_timeout())
        }

        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! generate_panel_start_impl(panel_job)

        case cannotRestart(reason: Throwable) => {
            val msg = Map(
                "type" -> "error",
                "error" -> "cannot generate panel"
            )
            alWebSocket(panel_job.uid).post(msg)
            log.info(s"reason is ${reason}")
            self ! generate_panel_end(panel_job.uid, toJson("cannot generate panel"))
        }

        case msg : AnyRef => log.info(s"Warning! Message not delivered. alGeneratePanelCameo.received_msg=${msg}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(30 minute) {
        self ! generate_panel_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        comeoActor ! msg
        log.info("stopping generate panel cameo")
        timeoutMessager.cancel()
        self ! PoisonPill
    }
}
