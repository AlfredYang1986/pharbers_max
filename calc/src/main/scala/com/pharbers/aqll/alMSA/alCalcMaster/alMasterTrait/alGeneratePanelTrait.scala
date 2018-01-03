package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.stm._
import scala.concurrent.Await
import akka.routing.BroadcastPool
import scala.concurrent.duration._
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alMSA.alMaxSlaves.alGeneratePanelSlave
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.panelMsg._
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole

/**
  * Created by jeorch on 17-8-31.
  *     Modify by clock on 2017.12.19
  */
trait alGeneratePanelTrait { this : Actor =>
    val panel_router = createGeneratePanelRouter
    val generate_panel_jobs = Ref(List[alPanelItem]())
    //TODO shijan chuancan
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

    def pushGeneratePanelJobs(item : alPanelItem) = {
        atomic { implicit thx =>
            generate_panel_jobs() = generate_panel_jobs() :+ item
        }
    }

    //TODO ask shenyong
    def canGeneratePanelJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitgeneratepanelslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0
    }

    def generatePanelScheduleJobs = {
        if (canGeneratePanelJob) {
            atomic { implicit thx =>
                val tmp = generate_panel_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    generate_panel_jobs() = generate_panel_jobs().tail
                    doGeneratePanelJob(tmp.head)
                }
            }
        }
    }

    def doGeneratePanelJob(panelJob: alPanelItem) = {
        val cur = context.actorOf(alCameoGeneratePanel.props(panelJob, panel_router))
        cur ! generate_panel_start()
    }
}

object alCameoGeneratePanel {
    def props(panel_job : alPanelItem,
              slaveActor : ActorRef) = Props(new alCameoGeneratePanel(panel_job, slaveActor))
}

class alCameoGeneratePanel(panel_job : alPanelItem,
                           slaveActor : ActorRef) extends Actor with ActorLogging {
    override def receive = {
        case generate_panel_start() => slaveActor ! generate_panel_hand()
        case generate_panel_hand() =>
            sender ! generate_panel_start_impl(panel_job)
            shutCameo
        case msg: AnyRef =>
            alTempLog(s"Warning! Message not delivered. alCameoGeneratePanel.received_msg=$msg")
            shutCameo
    }

    def shutCameo = {
        alTempLog("stopping generate panel cameo")
        self ! PoisonPill
    }
}
