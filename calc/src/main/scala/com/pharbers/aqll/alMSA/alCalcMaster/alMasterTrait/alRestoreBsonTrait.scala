package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.stm._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alMSA.alMaxSlaves.alRestoreBsonSlave
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.restoreMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole

/**
  * Created by jeorch on 17-10-30.
  *     Modify by clock on 2017.12.21
  */
trait alRestoreBsonTrait { this : Actor =>
    val restore_router = createRestoreBsonRouter
    val restore_jobs = Ref(List[(String, String)]())
    //TODO shijian chuan can
    val restore_schdule = context.system.scheduler.schedule(3 second, 3 second, self, restoreBsonSchedule())

    def createRestoreBsonRouter = context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitrestorebsonslave")
                )
            ).props(alRestoreBsonSlave.props), name = "restore-bson-router")

    def pushRestoreJobs(uid: String, panel: String) = {
        atomic { implicit thx =>
            restore_jobs() = restore_jobs() :+ (uid, panel)
        }
    }

    //TODO ask shenyong
    def canRestoreJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitrestorebsonslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0
    }

    def restoreSchduleJobs = {
        if (canRestoreJob) {
            atomic { implicit thx =>
                val tmp = restore_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    restore_jobs() = restore_jobs().tail
                    restoreBson(tmp.head._1, tmp.head._2)
                }
            }
        }
    }

    def restoreBson(uid: String, panel: String) = {
        val cur = context.actorOf(alCameoRestoreBson.props(uid, panel, restore_router))
        cur ! restore_bson_start()
    }
}

object alCameoRestoreBson {
    def props(uid: String, panel: String, slaveActor: ActorRef) = Props(new alCameoRestoreBson(uid, panel, slaveActor))
}

class alCameoRestoreBson(uid: String, panel: String, slaveActor: ActorRef) extends Actor with ActorLogging {
    var start = false

    override def receive: Receive = {
        case restore_bson_start() => slaveActor ! restore_bson_hand()
        case restore_bson_hand() =>
            if (!start) {
                sender ! restore_bson_start_impl(uid, panel)
                start = true
            }
            shutCameo
        case msg: AnyRef =>
            alTempLog(s"Warning! Message not delivered. alCameoRestoreBson.received_msg=$msg")
            shutCameo
    }

    def shutCameo = {
        alTempLog("stopping restore bson cameo")
        self ! PoisonPill
    }
}

