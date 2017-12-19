package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import scala.concurrent.stm._
import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, Props}

import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alMSA.alMaxCmdJob.alCmdActor
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.scpMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoScp._
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{scpResult, scpSchedule}
import com.pharbers.aqll.common.alFileHandler.fileConfig.{group, memorySplitFile, scpPath, sync, user}
import com.typesafe.config.ConfigFactory

/**
  * Created by clock on 17-9-6.
  */
trait alScpQueueTrait { this: Actor =>
    val residueRunNumber = Ref(4)
    val scpQueue = Ref[List[alMaxRunning]](Nil)
    val scan_queue_schedule = context.system.scheduler.schedule(0 second, 1 second, self, scpSchedule())

    def pushScpJobs(item: alMaxRunning) = {
        atomic { implicit thx =>
            scpQueue() = scpQueue() :+ item
        }
    }

    def schduleScpJobs = {
        if(residueRunNumber.single.get > 0) {
            scpQueue.single.get match {
                case head :: tail => {
                    atomic{implicit what =>
                        scpQueue() = tail
                        residueRunNumber() = residueRunNumber() - 1
                        doScpJob(head)
                    }
                }
                case Nil => Unit
                case _ => throw new Exception("queue error")
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
    case class scp_pkg()
    case class scp_unpkg()
    case class scp_timeout()

    def props(item: alMaxRunning) = Props(new alCameoScp(item))
}

class alCameoScp(item: alMaxRunning) extends Actor with ActorLogging {
    override def receive: Receive = {
        case scp_pkg() => pkg
        case pkgend(s) => scp
        case scpend(s) => end
        case scp_timeout() => println("=====scp_timeout")
        case msg : AnyRef => log.info(s"Warning! Message not delivered. alCameoCalcYM.received_msg=${msg}")
    }

    def pkg ={
        val cmdActor = context.actorOf(alCmdActor.props())

        val sync_map = Map(
            "file" -> s"${memorySplitFile}${sync}${item.tid}",
            "target" -> s"${memorySplitFile}${sync}${item.tid}"
        )
        val group_map = Map(
            "file" -> s"${memorySplitFile}${group}${item.tid}",
            "target" -> s"${memorySplitFile}${group}${item.tid}"
        )

        cmdActor ! pkgmsgMuti(sync_map :: group_map :: Nil)
    }

    def scp ={
        val targetHost = ConfigFactory.load("split-calc-slave").getString("akka.remote.netty.tcp.hostname")
        val cmdActor = context.actorOf(alCmdActor.props())

        val sync_map = Map(
            "file" -> s"${memorySplitFile}${sync}${item.tid}.tar.gz",
            "target" -> s"${scpPath}${sync}/"
        )
        val group_map = Map(
            "file" -> s"${memorySplitFile}${group}${item.tid}.tar.gz",
            "target" -> s"${scpPath}${group}/"
        )

        cmdActor ! scpmsgMutiPath(sync_map :: group_map :: Nil, targetHost, user)
    }

    def end ={
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        a ! scpResult(item)
        shutCameo
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val scp_timer = context.system.scheduler.scheduleOnce(30 minute) {
        self ! scp_timeout()
    }

    def shutCameo = {
        log.info("stopping scp cameo")
        scp_timer.cancel()
        context.stop(self)
    }
}