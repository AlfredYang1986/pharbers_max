package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import scala.concurrent.stm._
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alMaxCmdJob.alCmdActor
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.scpMsg._
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.common.alFileHandler.fileConfig.{group, memorySplitFile, scpPath, sync, user}

/**
  * Created by clock on 17-9-6.
  *     Modify by clock on 2017.12.19
  */
trait alScpQueueTrait { this: Actor =>
    val residueRunNumber = Ref(4)
    val scpQueue = Ref[List[alMaxRunning]](Nil)
    //TODO shijian chuan can
    val scan_queue_schedule = context.system.scheduler.schedule(0 second, 1 second, self, scpSchedule())

    def pushScpJobs(item: alMaxRunning) = {
        atomic { implicit thx =>
            scpQueue() = scpQueue() :+ item
        }
    }

    def scpSchduleJobs = {
        if (residueRunNumber.single.get > 0) {
            atomic { implicit thx =>
                val tmp = scpQueue.single.get
                if (tmp.isEmpty) Unit
                else {
                    scpQueue() = scpQueue().tail
                    residueRunNumber() = residueRunNumber() - 1
                    doScpJob(tmp.head)
                }
            }
        }
    }

    def doScpJob(item: alMaxRunning) = {
        val cmdActor = context.actorOf(alCameoScp.props(item))
        cmdActor ! scp_pkg()
    }

    def releaseScpEnergy = {
        atomic { implicit thx =>
            residueRunNumber() = residueRunNumber.single.get + 1
        }
    }
}

object alCameoScp {
    def props(item: alMaxRunning) = Props(new alCameoScp(item))
}

class alCameoScp(item: alMaxRunning) extends Actor with ActorLogging {
    //TODO shijian chuan can
    val timeoutMessager = context.system.scheduler.scheduleOnce(30 minute) {
        self ! scp_timeout()
    }

    override def receive: Receive = {
        case scp_pkg() => pkg_job
        case pkgend(_) => scp_job
        case scpend(_) => scp_end
        case scp_timeout() => println("=====scp_timeout")
        case msg : AnyRef => log.info(s"Warning! Message not delivered. alCameoCalcYM.received_msg=${msg}")
    }

    def pkg_job ={
        val cmdActor = context.actorOf(alCmdActor.props())

        val sync_map = Map(
            "file" -> s"$memorySplitFile$sync${item.tid}",
            "target" -> s"$memorySplitFile$sync${item.tid}"
        )
        val group_map = Map(
            "file" -> s"$memorySplitFile$group${item.tid}",
            "target" -> s"$memorySplitFile$group${item.tid}"
        )

        cmdActor ! pkgmsgMuti(sync_map :: group_map :: Nil)
    }

    def scp_job ={
        val targetHost = ConfigFactory.load("split-calc-slave").getString("akka.remote.netty.tcp.hostname")
        val cmdActor = context.actorOf(alCmdActor.props())

        val sync_map = Map(
            "file" -> s"$memorySplitFile$sync${item.tid}.tar.gz",
            "target" -> s"$scpPath}$sync"
        )
        val group_map = Map(
            "file" -> s"$memorySplitFile$group${item.tid}.tar.gz",
            "target" -> s"$scpPath$group"
        )

        cmdActor ! scpmsgMutiPath(sync_map :: group_map :: Nil, targetHost, user)
    }

    def scp_end ={
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        a ! scpResult(item)
        shutCameo
    }

    def shutCameo = {
        timeoutMessager.cancel()

        log.info("stop scp cameo")
        alTempLog("stop scp cameo")

        self ! PoisonPill
    }
}