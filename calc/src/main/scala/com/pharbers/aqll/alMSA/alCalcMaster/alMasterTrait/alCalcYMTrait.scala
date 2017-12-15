package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.pattern.ask
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.masterIP
import scala.concurrent.stm._
import scala.concurrent.duration._
import akka.routing.BroadcastPool
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcYMSlave
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.util.Timeout
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGeneratePanel.generate_panel_timeout
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{calcYMResult, calcYMSchedule}

import scala.concurrent.Await

/**
  * Created by jeorch on 17-10-11.
  */
trait alCalcYMTrait { this : Actor =>
    import scala.concurrent.ExecutionContext.Implicits.global

    val calcYM_router = createCalcYMRouter
    val calc_ym_jobs = Ref(List[alPanelItem]())
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
            calc_ym_jobs() = calc_ym_jobs() :+ item
        }
    }

    def canSchduleYMJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitcalcymslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0        // TODO：现在只有一个，以后由配置文件修改
    }

    def calcYMScheduleJobs = {
        if (canSchduleYMJob) {
            atomic { implicit thx =>
                val tmp = calc_ym_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    calc_ym_jobs() = calc_ym_jobs().tail
                    doCalcYMJob(tmp.head)
                }
            }
        }
    }

    def doCalcYMJob(calcYM_job: alPanelItem) = {
        import alCameoCalcYM._
        val cur = context.actorOf(alCameoCalcYM.props(calcYM_job, self, calcYM_router))
        cur ! calcYM_start()
    }

}

object alCameoCalcYM {
    case class calcYM_start()
    case class calcYM_hand()
    case class calcYM_start_impl(panel_job: alPanelItem)
    case class calcYM_end(result: Boolean, ym: String)
    case class calcYM_timeout()

    def props(calcYM_job: alPanelItem,
              masterActor: ActorRef,
              slaveActor: ActorRef) = Props(new alCameoCalcYM(calcYM_job, masterActor, slaveActor))
}

class alCameoCalcYM(calcYM_job: alPanelItem,
                    masterActor: ActorRef,
                    slaveActor: ActorRef) extends Actor with ActorLogging {
    import alCameoCalcYM._

    override def receive: Receive = {
        case calcYM_start() => slaveActor ! calcYM_hand()
        case calcYM_hand() => sender ! calcYM_start_impl(calcYM_job)
        case calcYM_end(result, ym) => {
            self ! calcYMResult(ym)
            shutCameo
        }
        case calcYM_timeout() => println("=====calcYM_timeout")
        case msg : AnyRef => log.info(s"Warning! Message not delivered. alCameoCalcYM.received_msg=${msg}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val calcYM_timer = context.system.scheduler.scheduleOnce(30 minute) {
        self ! calcYM_timeout()
    }

    def shutCameo = {
        log.info("stopping generate panel cameo")
        calcYM_timer.cancel()
        context.stop(self)
    }
}
