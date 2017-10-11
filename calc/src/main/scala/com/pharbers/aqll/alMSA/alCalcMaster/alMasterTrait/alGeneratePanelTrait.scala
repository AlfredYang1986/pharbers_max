package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.BroadcastPool
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver._
import com.pharbers.aqll.alMSA.alMaxSlaves.alGeneratePanelSlave
import com.pharbers.aqll.alStart.alHttpFunc.{alUpBeforeItem, alUploadItem}
import com.pharbers.pfizer.impl.phPfizerHandleImpl
import play.api.libs.json.JsString

import scala.collection.immutable.Map
import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by jeorch on 17-8-31.
  */
trait alGeneratePanelTrait { this : Actor =>

    def createGeneratePanelRouter =
        context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitgeneratepanelslave")
                )
            ).props(alGeneratePanelSlave.props), name = "generate-panel-router")

    val panel_router = createGeneratePanelRouter

    val generate_panel_jobs = Ref(List[(alUploadItem, ActorRef)]())

    import scala.concurrent.ExecutionContext.Implicits.global
    val panelLimit = Ref(2)

    val generate_panel_schedule = context.system.scheduler.schedule(1 second, 1 second, self, generatePanelSchedule())

    def push_generate_panel_jobs(item : alUploadItem, s : ActorRef) = {
        atomic { implicit thx =>
            generate_panel_jobs() = generate_panel_jobs() :+ (item, s)
        }
    }
    def generate_panel_schedule_jobs = {
        if (panelLimit.single.get > 0) {
            atomic { implicit thx =>
                val tmp = generate_panel_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    panelLimit() = panelLimit.single.get - 1
                    generate_panel_jobs() = generate_panel_jobs().tail
                    do_generate_panel_job(tmp.head._1, tmp.head._2)
                }
            }
        }
    }
    def do_generate_panel_job(panel_job : alUploadItem, s : ActorRef) = {
        val cur = context.actorOf(alCameoGeneratePanel.props(panel_job, s, self, panel_router))
        import alCameoGeneratePanel._
        cur ! generate_panel_start()
    }
    def release_panel_energy = {
        atomic { implicit thx =>
            panelLimit() = panelLimit.single.get + 1
        }
    }

}

object alCameoGeneratePanel {

    case class generate_panel_start()
    case class generate_panel_hand()
    case class generate_panel_start_impl(panel_job: alUploadItem)
    case class generate_panel_end(result : Boolean, file_path : String)
    case class generate_panel_timeout()

    def props(panel_job : alUploadItem,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoGeneratePanel(panel_job, originSender, owner, router))
}

class alCameoGeneratePanel(val panel_job : alUploadItem,
                           val originSender : ActorRef,
                           val owner : ActorRef,
                           val router : ActorRef) extends Actor with ActorLogging {

    import alCameoGeneratePanel._

    override def receive: Receive = {

        case generate_panel_start() => router ! generate_panel_hand()
        case generate_panel_hand() => sender ! generate_panel_start_impl(panel_job)
        case generate_panel_end(result, file_path) => {
            owner ! releasePanelEnergy()
            owner ! generatePanelResult(file_path)
            shutCameo(generate_panel_end(result, file_path))
        }
        case msg : AnyRef => log.info(s"Warning! Message not delivered. alCameoGeneratePanel.received_msg=${msg}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val generate_panel_timer = context.system.scheduler.scheduleOnce(30 minute) {
        self ! generate_panel_timeout()
    }

    def shutCameo(msg : AnyRef) = {
//        originSender ! msg
        log.info("stopping generate panel cameo")
        generate_panel_timer.cancel()
        context.stop(self)
    }
}
