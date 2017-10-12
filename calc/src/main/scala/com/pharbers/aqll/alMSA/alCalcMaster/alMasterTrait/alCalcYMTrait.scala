package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.{calcYMResult, calcYMSchedule, releaseCalcYMEnergy}
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcYMSlave
import com.pharbers.aqll.alStart.alHttpFunc.alUpBeforeItem

import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by jeorch on 17-10-11.
  */
trait alCalcYMTrait { this : Actor =>
    def createCalcYMRouter =
        context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitcalcymslave")
                )
            ).props(alCalcYMSlave.props), name = "calc-ym-router")

    val calcYM_router = createCalcYMRouter

    val calc_ym_jobs = Ref(List[(alUpBeforeItem, ActorRef)]())

    import scala.concurrent.ExecutionContext.Implicits.global
    val calcYMLimit = Ref(1)
    val calc_ym_schedule = context.system.scheduler.schedule(1 second, 1 second, self, calcYMSchedule())

    def push_calc_ym_jobs(item : alUpBeforeItem, s : ActorRef) = {
        atomic { implicit thx =>
            calc_ym_jobs() = calc_ym_jobs() :+ (item, s)
        }
    }
    def calc_ym_schedule_jobs = {
        if (calcYMLimit.single.get > 0) {
            atomic { implicit thx =>
                val tmp = calc_ym_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    calcYMLimit() = calcYMLimit.single.get - 1
                    calc_ym_jobs() = calc_ym_jobs().tail
                    do_calc_ym_job(tmp.head._1, tmp.head._2)
                }
            }
        }
    }
    def do_calc_ym_job(calcYM_job : alUpBeforeItem, s : ActorRef) = {

        println(s" && do_generate_panel_job")

        val cur = context.actorOf(alCameoCalcYM.props(calcYM_job, s, self, calcYM_router))
        import alCameoCalcYM._
        cur ! calcYM_start()
    }
    def release_calcYM_energy = {
        atomic { implicit thx =>
            calcYMLimit() = calcYMLimit.single.get + 1
        }
    }
}

object alCameoCalcYM {

    case class calcYM_start()
    case class calcYM_hand()
    case class calcYM_start_impl(panel_job: alUpBeforeItem)
    case class calcYM_end(result : Boolean, ym : String)
    case class calcYM_timeout()

    def props(calcYM_job : alUpBeforeItem,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoCalcYM(calcYM_job, originSender, owner, router))
}

class alCameoCalcYM(val calcYM_job : alUpBeforeItem,
                           val originSender : ActorRef,
                           val owner : ActorRef,
                           val router : ActorRef) extends Actor with ActorLogging {

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
        //        originSender ! msg
        log.info("stopping generate panel cameo")
        calcYM_timer.cancel()
        context.stop(self)
    }
}
