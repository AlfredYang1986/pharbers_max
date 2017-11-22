package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcStep, alMaxRunning}
import com.pharbers.aqll.alMSA.alMaxSlaves.alSplitPanelSlave
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.splitPanelSchedule

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by clock on 12/07/2017.
  */
trait alSplitPanelTrait { this : Actor =>
    import scala.concurrent.ExecutionContext.Implicits.global

    val split_router = createSplitExcelRouter
    val split_jobs = Ref(List[(alMaxRunning, ActorRef)]())
    val split_schdule = context.system.scheduler.schedule(1 second, 1 second, self, splitPanelSchedule())

    def createSplitExcelRouter = context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitsplitpanelslave")
                )
            ).props(alSplitPanelSlave.props), name = "split-excel-router")
    def pushSplitPanelJob(item: alMaxRunning, s : ActorRef) = {
        println("aaaaaaa")
        atomic { implicit thx =>
            split_jobs() = split_jobs() :+ (item, s)
        }
    }
    def canSchduleSplitPanelJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitsplitpanelslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0        // TODO：现在只有一个，以后由配置文件修改
    }
    def schduleSplitPanelJob = {
        if (canSchduleSplitPanelJob) {
            atomic { implicit thx =>
                val tmp = split_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    splitPanel(tmp.head._1, tmp.head._2)
                    split_jobs() = split_jobs().tail
                }
            }
        }
    }
    def splitPanel(item: alMaxRunning, s : ActorRef) = {
        import alCameoSplitPanel._
        val cur = context.actorOf(alCameoSplitPanel.props(item, s, self, split_router))
        cur ! split_panel_start()
    }
}

object alCameoSplitPanel {
    case class split_panel_start()
    case class split_panel_hand()
    case class split_panel_start_impl(item: alMaxRunning)
    case class split_panel_end(result : Boolean, item: alMaxRunning)
    case class split_panel_timeout()

    def props(item: alMaxRunning,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoSplitPanel(item, originSender, owner, router))
}

class alCameoSplitPanel(item: alMaxRunning,
                        originSender : ActorRef,
                        owner : ActorRef,
                        router : ActorRef) extends Actor with ActorLogging {
    import alCameoSplitPanel._

    var sign = false

    override def receive: Receive = {
        case split_panel_timeout() => {
            log.debug("timeout occur")
            shutCameo(split_panel_timeout())
        }

        case _ : split_panel_start => router ! split_panel_hand()

        case split_panel_hand() => {
            if (item.step == alCalcStep().PANEL) {
                sender ! split_panel_start_impl(item)
                item.step = alCalcStep().PANEL
            }
        }

        case result : split_panel_end => {
            shutCameo(result)
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val split_timer = context.system.scheduler.scheduleOnce(10 minute) {
        self ! split_panel_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping split excel cameo")
        split_timer.cancel()
        context.stop(self)
    }
}
