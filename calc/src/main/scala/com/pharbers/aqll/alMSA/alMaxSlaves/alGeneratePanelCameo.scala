package com.pharbers.aqll.alMSA.alMaxSlaves

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{canDoRestart, canIReStart, cannotRestart}
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGeneratePanel.{generate_panel_end, generate_panel_start_impl, generate_panel_timeout}
import com.pharbers.aqll.alStart.alHttpFunc.alUploadItem
import com.pharbers.panel.pfizer.phPfizerHandle
import play.api.libs.json.{JsString, JsValue}

import scala.collection.immutable.Map

/**
  * Created by jeorch on 17-10-11.
  */
object alGeneratePanelCameo {
    def props(panel_job : alUploadItem,
              originSender : ActorRef,
              owner : ActorRef,
              counter : ActorRef) = Props(new alGeneratePanelCameo(panel_job, originSender, owner, counter))
}
class alGeneratePanelCameo(val panel_job : alUploadItem,
                           val originSender : ActorRef,
                           val owner : ActorRef,
                           val counter : ActorRef) extends Actor with ActorLogging {

    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {

        case generate_panel_start_impl(panel_job) => {
            println("3.开始生成panel")
            val args: Map[String, List[String]] = Map(
                "company" -> List(panel_job.company),
                "user" -> List(panel_job.user),
                "cpas" -> panel_job.cpas.split("&").toList,
                "gycxs" -> panel_job.gycxs.split("&").toList
            )

            def getResult(data: JsValue) ={
                data.as[Map[String, JsValue]].map{ x =>
                    x._1 -> x._2.as[Map[String, JsValue]].map{y =>
                        y._1 -> y._2.as[List[String]]
                    }
                }
            }

            val data_parse = phPfizerHandle(args)
            val yms = data_parse.calcYM.asInstanceOf[JsString].value
            val lst = yms.split("#").toList
            println("lst = " + lst.toString)
            val result = getResult(data_parse.getPanelFile(lst))
            val panelLst = result.values.flatMap(_.values).toList.flatten
            println("panel = " + panelLst.toString)
            alMessageProxy().sendMsg(panelLst.toString, panel_job.user, Map("type" -> "txt"))
            self ! generate_panel_end(true, panelLst)
        }
        case generate_panel_end(result, paths) => {
            owner forward generate_panel_end(result, paths)
            shutSlaveCameo(generate_panel_end(result, paths))
        }
        case generate_panel_timeout() => {
            log.debug("timeout occur")
            shutSlaveCameo(generate_panel_timeout())
        }

        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! generate_panel_start_impl(panel_job)

        case cannotRestart(reason: Throwable) => {
            new alMessageProxy().sendMsg("cannot generate panel", panel_job.user, Map("type" -> "txt"))
            log.info(s"reason is ${reason}")
            self ! generate_panel_end(false, "cannot generate panel" :: Nil)
        }

        case msg : AnyRef => log.info(s"Warning! Message not delivered. alGeneratePanelCameo.received_msg=${msg}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(30 minute) {
        self ! generate_panel_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.info("stopping generate panel cameo")
        timeoutMessager.cancel()
//        context.stop(self)
        self ! PoisonPill
    }
}
