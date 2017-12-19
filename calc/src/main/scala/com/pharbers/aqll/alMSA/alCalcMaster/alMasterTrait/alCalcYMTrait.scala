package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.stm._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcYMSlave
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.ymMsg._
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole

/**
  * Created by jeorch on 17-10-11.
  *     Modify by clock on 2017.12.19
  */
trait alCalcYMTrait { this : Actor =>
    val calcYM_router = createCalcYMRouter
    val calcYMJobs = Ref(List[alPanelItem]())
    //TODO shijian chuan can
    val calc_ym_schedule = context.system.scheduler.schedule(1 second, 1 second, self, calcYMSchedule())

    def createCalcYMRouter = context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitcalcymslave")
                )
            ).props(alCalcYMSlave.props), name = "calc-ym-router")

    def pushCalcYMJobs(item: alPanelItem) = {
        atomic { implicit thx =>
            calcYMJobs() = calcYMJobs() :+ item
        }
    }

    //TODO ask shenyong
    def canCalcYMJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitcalcymslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0
    }

    def calcYMScheduleJobs = {
        if (canCalcYMJob) {
            atomic { implicit thx =>
                val tmp = calcYMJobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    calcYMJobs() = calcYMJobs().tail
                    doCalcYMJob(tmp.head)
                }
            }
        }
    }

    def doCalcYMJob(calcYMJob: alPanelItem) = {
        val cur = context.actorOf(alCameoCalcYM.props(calcYMJob, calcYM_router))
        cur ! calcYM_start()
    }
}

object alCameoCalcYM {
    def props(calcYMJob: alPanelItem,
              slaveActor: ActorRef) = Props(new alCameoCalcYM(calcYMJob, slaveActor))
}

class alCameoCalcYM(calcYM_job: alPanelItem, slaveActor: ActorRef) extends Actor with ActorLogging {
    override def receive: Receive = {
        case calcYM_start() => slaveActor ! calcYM_hand()
        case calcYM_hand() =>
            sender ! calcYM_start_impl(calcYM_job)
            shutCameo
        case msg: AnyRef =>
            alTempLog(s"Warning! Message not delivered. alCameoCalcYM.received_msg=$msg")
            shutCameo
    }

    def shutCameo = {
        alTempLog("stopping calc ym cameo")
        self ! PoisonPill
    }
}
