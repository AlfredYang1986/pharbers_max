package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.stm._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.routing.BroadcastPool
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alMSA.alMaxSlaves.alSplitPanelSlave
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{splitPanelResult, splitPanelSchedule}

/**
  * Created by clock on 12/07/2017.
  */
trait alSplitPanelTrait { this : Actor =>
    import scala.concurrent.ExecutionContext.Implicits.global

    val split_router = createSplitExcelRouter
    val split_jobs = Ref(List[alMaxRunning]())
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
    def pushSplitPanelJob(item: alMaxRunning) = {
        atomic { implicit thx =>
            split_jobs() = split_jobs() :+ item
        }
    }
    def canSchduleSplitPanelJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitsplitpanelslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0        // TODO：现在只有一个，以后由配置文件修改
    }
    def schduleSplitPanelJob = {
        if (canSchduleSplitPanelJob) {
            atomic { implicit thx =>
                val tmp = split_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    doSplitPanel(tmp.head)
                    split_jobs() = split_jobs().tail
                }
            }
        }
    }
    def doSplitPanel(item: alMaxRunning) = {
        import alCameoSplitPanel._
        val cur = context.actorOf(alCameoSplitPanel.props(item, self, split_router))
        cur ! split_panel_start()
    }
}

object alCameoSplitPanel {
    case class split_panel_start()
    case class split_panel_hand()
    case class split_panel_start_impl(item: alMaxRunning)
    case class split_panel_end(item: alMaxRunning, parent: String, subs: List[String])
    case class split_panel_timeout()

    def props(item: alMaxRunning,
              masterActor: ActorRef,
              slaveActor: ActorRef) = Props(new alCameoSplitPanel(item, masterActor, slaveActor))
}

class alCameoSplitPanel(item: alMaxRunning,
                        masterActor: ActorRef,
                        slaveActor: ActorRef) extends Actor with ActorLogging {
    import alCameoSplitPanel._

    override def receive: Receive = {
        case split_panel_timeout() => {
            log.debug("timeout occur")
            println("=====split_panel_timeout")
//            shutCameo(split_panel_timeout())
        }

        case split_panel_start() => slaveActor ! split_panel_hand()

        case split_panel_hand() => {
            sender ! split_panel_start_impl(item)
        }

        case split_panel_end(result, p, sb) => {
            masterActor ! splitPanelResult(result, p, sb)
            shutCameo
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val split_timer = context.system.scheduler.scheduleOnce(10 minute) {
        self ! split_panel_timeout()
    }

    def shutCameo = {
        log.debug("stopping split excel cameo")
        split_timer.cancel()
        context.stop(self)
    }
}
