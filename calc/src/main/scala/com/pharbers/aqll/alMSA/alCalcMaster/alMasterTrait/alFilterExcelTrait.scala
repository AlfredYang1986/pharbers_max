package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.{queryIdleNodeInstanceInSystemWithRole, refundNodeForRole}
import com.pharbers.aqll.alMSA.alMaxSlaves.alFilterExcelSlave

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.stm._

/**
  * Created by alfredyang on 11/07/2017.
  */

trait alFilterExcelTrait { this : Actor =>

    // TODO : query instance from agent
    def createFilterExcelRouter =
        context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
            ClusterRouterPoolSettings(
                totalInstances = 1,
                maxInstancesPerNode = 1,
                allowLocalRoutees = false,
                useRole = Some("splitfilterexcelslave")
            )
        ).props(alFilterExcelSlave.props), name = "filter-excel-router")

    val filter_router = createFilterExcelRouter

    def pushFilterJob(file : String, par : alCalcParmary, s : ActorRef) = {
        atomic { implicit thx =>
            filter_jobs() = filter_jobs() :+ (file, par, s)
        }
    }

    def canSchduleJob : Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitfilterexcelslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0        // TODO：现在只有一个，以后由配置文件修改
    }

    def schduleFilterJob = {
        if (canSchduleJob) {
            atomic { implicit thx =>
                val tmp = filter_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    filterExcel(tmp.head._1, tmp.head._2, tmp.head._3)
                    filter_jobs() = filter_jobs().tail
                }
            }
        }
    }

    def filterExcel(file : String, par : alCalcParmary, s : ActorRef) = {
        val cur = context.actorOf(alCameoFilterExcel.props(file, par, s, self, filter_router))
        import alCameoFilterExcel._
        cur ! filter_excel_start()
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val filter_schdule = context.system.scheduler.schedule(1 second, 1 second, self, filter_excel_schedule())

    val filter_jobs = Ref(List[(String, alCalcParmary, ActorRef)]())
    case class filter_excel_schedule()
}

object alCameoFilterExcel {
    case class filter_excel_start()
    case class filter_excel_hand()
    case class filter_excel_start_impl(p : String, par : alCalcParmary)
    case class filter_excel_end(result : Boolean, cp: alCalcParmary)
    case class filter_excel_timeout()

    def props(file : String,
              par : alCalcParmary,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoFilterExcel(file, par, originSender, owner, router))
}

class alCameoFilterExcel(val file : String,
                         val par : alCalcParmary,
                         val originSender : ActorRef,
                         val owner : ActorRef,
                         val router : ActorRef) extends Actor with ActorLogging {

    import alCameoFilterExcel._

    var sign = false

    override def receive: Receive = {
        case filter_excel_timeout() => {
            log.debug("timeout occur")
            shutCameo(filter_excel_timeout())
        }
        case _ : filter_excel_start => router ! filter_excel_hand()
        case filter_excel_hand() => {
            if (sign == false) {
                sender ! filter_excel_start_impl(file, par)
                sign = true
            }
        }
        // TODO: 内存泄漏，稳定后修改
        case result : filter_excel_end => {
//            slaveStatus send slave_status(true)
//            owner forward result
            shutCameo(result)
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val filter_timer = context.system.scheduler.scheduleOnce(10 minute) {
        self ! filter_excel_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping filter excel cameo")
        filter_timer.cancel()
        context.stop(self)
    }
}
