package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxProperty
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_start
import com.pharbers.aqll.alMSA.alMaxSlaves.alCalcDataSlave

import scala.concurrent.stm._
import scala.concurrent.duration._

/**
  * Created by alfredyang on 13/07/2017.
  */
trait alCalcDataTrait { this : Actor =>
    def createCalcRouter =
        context.actorOf(
            ClusterRouterPool(BroadcastPool(2),
                ClusterRouterPoolSettings(
                    totalInstances = 2,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitcalcslave")
                )
            ).props(alCalcDataSlave.props), name = "calc-data-router")

    val calc_router = createCalcRouter

    def pushCalcJob(property : alMaxProperty, s : ActorRef) = {
        atomic { implicit thx =>
            calc_jobs() = calc_jobs() :+ (property, s)
        }
    }

    def canCalcGroupJob : Boolean = {
        true
    }

    def schduleCalcJob = {
        if (canCalcGroupJob) {
            atomic { implicit thx =>
                val tmp = calc_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    calcData(tmp.head._1, tmp.head._2)
                    calc_jobs() = calc_jobs().tail
                }
            }
        }
    }

    def calcData(property : alMaxProperty, s : ActorRef) {
        val cur = context.actorOf(alCameoCalcData.props(property, s, self, calc_router))
        cur ! calc_data_start()
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val calc_schdule = context.system.scheduler.schedule(1 second, 1 second, self, calc_schedule())

    val calc_jobs = Ref(List[(alMaxProperty, ActorRef)]())
    case class calc_schedule()
}

object alCameoCalcData {
    case class calc_data_start()
    case class calc_data_hand()
    case class calc_data_start_impl(sub : alMaxProperty)
    case class calc_data_end(result : Boolean, property : alMaxProperty)
    case class calc_data_timeout()

    def props(property : alMaxProperty,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoCalcData(property, originSender, owner, router))
}

class alCameoCalcData (val property : alMaxProperty,
                        val originSender : ActorRef,
                        val owner : ActorRef,
                        val router : ActorRef) extends Actor with ActorLogging {

    import alCameoCalcData._

    var sed = 0
    var cur = 0
    var tol = 0

    override def receive: Receive = {
        case calc_data_timeout() => {
            log.debug("timeout occur")
            shutCameo(calc_data_timeout())
        }
        case _ : calc_data_start => {
            tol = property.subs.length
            router ! calc_data_hand()
        }
        case calc_data_hand() => {
            if (sed < tol) {
                println(s"trait hand $sed")
                val tmp = property.subs(sed)
                sender ! calc_data_start_impl(tmp)
                sed += 1
            }
        }
        case calc_data_end(result, mp) => {
            println("trait result")
            if (result) {
                cur += 1
                if (cur == tol) {
                    val r = calc_data_end(true, property)
                    owner ! r
                    shutCameo(r)
                }
            } else {
                val r = calc_data_end(false, property)
                owner ! r
                shutCameo(r)
            }
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val calc_timer = context.system.scheduler.scheduleOnce(10 minute) {
        self ! calc_data_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping group data cameo")
        context.stop(self)
    }
}