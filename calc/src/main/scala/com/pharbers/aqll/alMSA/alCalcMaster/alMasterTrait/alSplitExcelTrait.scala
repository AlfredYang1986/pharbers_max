package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alMSA.alMaxSlaves.alSplitExcelSlave

import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by alfredyang on 12/07/2017.
  */
trait alSplitExcelTrait { this : Actor =>
    def createSplitExcelRouter =
        context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitsplitexcelslave")
                )
            ).props(alSplitExcelSlave.props), name = "split-excel-router")

    val split_router = createSplitExcelRouter

    def pushSplitExcelJob(file : String, par : alCalcParmary, s : ActorRef) = {
        atomic { implicit thx =>
            split_jobs() = split_jobs() :+ (file, par, s)
        }
    }

    def canSchduleSplitExcelJob : Boolean = {
        true
    }

    def schduleSplitExcelJob = {
        if (canSchduleSplitExcelJob) {
            atomic { implicit thx =>
                val tmp = split_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    println(s"head is ${tmp.head}")
                    splitExcel(tmp.head._1, tmp.head._2, tmp.head._3)
                    split_jobs() = split_jobs().tail
                }
            }
        }
    }

    def splitExcel(file : String, par : alCalcParmary, s : ActorRef) = {
        val cur = context.actorOf(alCameoSplitExcel.props(file, par, s, self, split_router))
        // context.watch(cur)
        import alCameoSplitExcel._
        cur ! split_excel_start()
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val split_schdule = context.system.scheduler.schedule(1 second, 1 second, self, split_excel_schedule())

    val split_jobs = Ref(List[(String, alCalcParmary, ActorRef)]())
    case class split_excel_schedule()
}

object alCameoSplitExcel {
    case class split_excel_start()
    case class split_excel_hand()
    case class split_excel_start_impl(p : String, par : alCalcParmary)
    case class split_excel_end(result : Boolean, uuid : String, subs : List[String], par : alCalcParmary)
    case class split_excel_timeout()

    def props(file : String,
              par : alCalcParmary,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoSplitExcel(file, par, originSender, owner, router))
}

class alCameoSplitExcel (val file : String,
                         val par : alCalcParmary,
                         val originSender : ActorRef,
                         val owner : ActorRef,
                         val router : ActorRef) extends Actor with ActorLogging {

    import alCameoSplitExcel._

    var sign = false

    override def receive: Receive = {
        case split_excel_timeout() => {
            log.debug("timeout occur")
            shutCameo(split_excel_timeout())
        }
        case _ : split_excel_start => router ! split_excel_hand()
        case split_excel_hand() => {
            if (sign == false) {
                sender ! split_excel_start_impl(file, par)
                sign = true
            }
        }
        case result : split_excel_end => {
            owner forward result
            shutCameo(result)
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val split_timer = context.system.scheduler.scheduleOnce(10 minute) {
        self ! split_excel_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping split excel cameo")
        context.stop(self)
    }
}
