package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alMSA.alMaxSlaves.alFilterExcelSlave

import scala.concurrent.stm._
import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */
class alGroupIntegrateDataTrait { this : Actor =>
    def createGroupRouter =
        context.actorOf(
            ClusterRouterPool(BroadcastPool(2),
                ClusterRouterPoolSettings(
                    totalInstances = 2,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitgroupslave")
                )
            ).props(alFilterExcelSlave.props), name = "group-integrate-router") // TODO : group slave

    val group_router = createGroupRouter

    def pushGroupJob(file : String, par : alCalcParmary, s : ActorRef) = {
        atomic { implicit thx =>
            group_jobs() = group_jobs() :+ (file, par, s)
        }
    }

    def canSchduleGroupJob : Boolean = {
        true
    }

    def schduleGroupJob = {
        if (canSchduleGroupJob) {
            atomic { implicit thx =>
                val tmp = group_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    groupData(tmp.head._1, tmp.head._2, tmp.head._3)
                    group_jobs() = group_jobs().tail
                }
            }
        }
    }

    def groupData(file : String, par : alCalcParmary, s : ActorRef) = {
        val cur = context.actorOf(alCameoSplitExcel.props(file, par, s, self, group_router))
        // context.watch(cur)
//        import alCameoSplitExcel._
//        cur ! split_excel_start()
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val group_schdule = context.system.scheduler.schedule(1 second, 1 second, self, split_excel_schedule())

    val group_jobs = // Ref(List[(String, alCalcParmary, ActorRef)]())
    case class group_schedule()
}

object alCameoGroupDataExcel {
    case class group_data_start()
    case class group_data_hand()
    case class group_data_start_impl(p : String, par : alCalcParmary)
    case class group_data_end(result : Boolean, uuid : String, subs : List[String], par : alCalcParmary)
    case class group_data_timeout()

    def props(file : String,
              par : alCalcParmary,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoSplitExcel(file, par, originSender, owner, router))
}

class alCameoGroupDataExcel (val file : String,
                             val par : alCalcParmary,
                             val originSender : ActorRef,
                             val owner : ActorRef,
                             val router : ActorRef) extends Actor with ActorLogging {

    import alCameoGroupDataExcel._


    override def receive: Receive = {
        case group_data_timeout() => {
            log.debug("timeout occur")
            shutCameo(group_data_timeout())
        }
        case _ : group_data_start => router ! group_data_hand()
        case group_data_hand() => {
            // TODO : group data logic
        }
        case result : group_data_end => {
            owner forward result
            shutCameo(result)
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val split_timer = context.system.scheduler.scheduleOnce(10 minute) {
        self ! group_data_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping split excel cameo")
        context.stop(self)
    }
}
