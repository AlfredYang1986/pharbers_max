package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.stm._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alMSA.alMaxSlaves.alSplitPanelSlave
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.splitPanelMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole

/**
  * Created by clock on 12/07/2017.
  *     Modify by clock on 2017.12.19
  */
trait alSplitPanelTrait { this : Actor =>
    val split_router = createSplitPanelRouter
    val split_jobs = Ref(List[alMaxRunning]())
    //TODO shijian chuan can
    val split_schdule = context.system.scheduler.schedule(1 second, 1 second, self, splitPanelSchedule())

    def createSplitPanelRouter = context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitsplitpanelslave")
                )
            ).props(alSplitPanelSlave.props), name = "split-panel-router")

    def pushSplitPanelJobs(item: alMaxRunning) = {
        atomic { implicit thx =>
            split_jobs() = split_jobs() :+ item
        }
    }

    //TODO ask shenyong
    def canSplitPanelJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitsplitpanelslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0
    }

    def splitPanelSchduleJobs = {
        if (canSplitPanelJob) {
            atomic { implicit thx =>
                val tmp = split_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    split_jobs() = split_jobs().tail
                    doSplitPanel(tmp.head)
                }
            }
        }
    }

    def doSplitPanel(item: alMaxRunning) = {
        val cur = context.actorOf(alCameoSplitPanel.props(item, split_router))
        cur ! split_panel_start()
    }
}

object alCameoSplitPanel {
    def props(item: alMaxRunning,
              slaveActor: ActorRef) = Props(new alCameoSplitPanel(item, slaveActor))
}

class alCameoSplitPanel(item: alMaxRunning, slaveActor: ActorRef) extends Actor with ActorLogging {
    override def receive: Receive = {
        case split_panel_start() => slaveActor ! split_panel_hand()
        case split_panel_hand() =>
            sender ! split_panel_start_impl(item)
            shutCameo
        case msg: AnyRef =>
            alTempLog(s"Warning! Message not delivered. alCameoSplitPanel.received_msg=$msg")
            shutCameo
    }

    def shutCameo = {
        alTempLog("stopping split panel cameo")
        self ! PoisonPill
    }
}
