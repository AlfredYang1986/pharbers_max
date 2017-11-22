package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{calcYMResult, calcYMSchedule, releaseCalcYMEnergy}
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcYMSlave
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by jeorch on 17-10-11.
  */
trait alCalcYMTrait { this : Actor =>
    import scala.concurrent.ExecutionContext.Implicits.global

    val calcYMLimit = Ref(1)
    val calcYM_router = createCalcYMRouter
    val calc_ym_jobs = Ref(List[(alPanelItem, ActorRef)]())
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
    def pushCalcYMJobs(item : alPanelItem, s : ActorRef) = {
        atomic { implicit thx =>
            calc_ym_jobs() = calc_ym_jobs() :+ (item, s)
        }
    }
    def calcYMScheduleJobs = {
        if (calcYMLimit.single.get > 0) {
            atomic { implicit thx =>
                val tmp = calc_ym_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    calcYMLimit() = calcYMLimit.single.get - 1
                    calc_ym_jobs() = calc_ym_jobs().tail
                    doCalcYMJob(tmp.head._1, tmp.head._2)
                }
            }
        }
    }
    def doCalcYMJob(calcYM_job : alPanelItem, s : ActorRef) = {
        import alCameoCalcYM._
        val cur = context.actorOf(alCameoCalcYM.props(calcYM_job, s, self, calcYM_router))
        cur ! calcYM_start()
    }
    def releaseCalcYMEnergy = {
        atomic { implicit thx =>
            calcYMLimit() = calcYMLimit.single.get + 1
        }
    }
}

object alCameoCalcYM {
    case class calcYM_start()
    case class calcYM_hand()
    case class calcYM_start_impl(panel_job: alPanelItem)
    case class calcYM_end(result : Boolean, ym : String)
    case class calcYM_timeout()

    def props(calcYM_job : alPanelItem,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoCalcYM(calcYM_job, originSender, owner, router))
}

class alCameoCalcYM(val calcYM_job: alPanelItem,
                    val originSender: ActorRef,
                    val owner: ActorRef,
                    val router: ActorRef) extends Actor with ActorLogging {
    import alCameoCalcYM._

    override def receive: Receive = {
        case calcYM_start() => router ! calcYM_hand()
        case calcYM_hand() => sender ! calcYM_start_impl(calcYM_job)
        case calcYM_end(result, ym) => {
            owner ! releaseCalcYMEnergy()
            owner ! calcYMResult(ym)
            shutCameo(calcYM_end(result, ym))
        }
        case msg : AnyRef => log.info(s"Warning! Message not delivered. alCameoCalcYM.received_msg=${msg}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val calcYM_timer = context.system.scheduler.scheduleOnce(30 minute) {
        self ! calcYM_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        log.info("stopping generate panel cameo")
        calcYM_timer.cancel()
        context.stop(self)
    }
}
