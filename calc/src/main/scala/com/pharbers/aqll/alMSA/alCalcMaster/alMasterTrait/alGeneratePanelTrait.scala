package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.routing.BroadcastPool
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.util.Timeout
import akka.pattern.ask
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{queryIdleNodeInstanceInSystemWithRole, takeNodeForRole}
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver._
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{generatePanelResult, generatePanelSchedule}
import com.pharbers.aqll.alMSA.alMaxSlaves.alGeneratePanelSlave
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by jeorch on 17-8-31.
  */
trait alGeneratePanelTrait { this : Actor =>
    import scala.concurrent.ExecutionContext.Implicits.global

    val panel_router = createGeneratePanelRouter
    val generate_panel_jobs = Ref(List[(alPanelItem, ActorRef)]())
    val generate_panel_schedule = context.system.scheduler.schedule(1 second, 3 second, self, generatePanelSchedule())

    def createGeneratePanelRouter = context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitgeneratepanelslave")
                )
            ).props(alGeneratePanelSlave.props), name = "generate-panel-router")
    def pushGeneratePanelJobs(item : alPanelItem, s : ActorRef) = {
        atomic { implicit thx =>
            generate_panel_jobs() = generate_panel_jobs() :+ (item, s)
        }
    }
    def canSchdulePanelJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitgeneratepanelslave")
        // val f = a ? queryIdleNodeInstanceInSystemWithRole("splitcalcslave") // 在一台机器上实现和计算的互斥
        Await.result(f, t.duration).asInstanceOf[Int] > 0        // TODO：现在只有一个，以后由配置文件修改
    }
    def generatePanelScheduleJobs = {
        if (canSchdulePanelJob) {
            atomic { implicit thx =>
                val tmp = generate_panel_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    generate_panel_jobs() = generate_panel_jobs().tail
                    do_generate_panel_job(tmp.head._1, tmp.head._2)
                }
            }
        }
    }
    def do_generate_panel_job(panel_job : alPanelItem, s : ActorRef) = {
        import alCameoGeneratePanel._
        val cur = context.actorOf(alCameoGeneratePanel.props(panel_job, s, self, panel_router))
        cur ! generate_panel_start()
    }
}

object alCameoGeneratePanel {
    case class generate_panel_start()
    case class generate_panel_hand()
    case class generate_panel_start_impl(panel_job: alPanelItem)
    case class generate_panel_end(result : Boolean, paths : String)
    case class generate_panel_timeout()

    def props(panel_job : alPanelItem,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoGeneratePanel(panel_job, originSender, owner, router))
}

class alCameoGeneratePanel(val panel_job : alPanelItem,
                           val originSender : ActorRef,
                           val owner : ActorRef,
                           val router : ActorRef) extends Actor with ActorLogging {
    import alCameoGeneratePanel._

    override def receive: Receive = {
        case generate_panel_start() => router ! generate_panel_hand()
        case generate_panel_hand() => sender ! generate_panel_start_impl(panel_job)
        case generate_panel_end(result, panelLst) => {
            owner ! generatePanelResult(panelLst)
            shutCameo(generate_panel_end(result, panelLst))
        }
        case msg : AnyRef => log.info(s"Warning! Message not delivered. alCameoGeneratePanel.received_msg=${msg}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val generate_panel_timer = context.system.scheduler.scheduleOnce(30 minute) {
        self ! generate_panel_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        log.info("stopping generate panel cameo")
        generate_panel_timer.cancel()
        self ! PoisonPill
    }
}
