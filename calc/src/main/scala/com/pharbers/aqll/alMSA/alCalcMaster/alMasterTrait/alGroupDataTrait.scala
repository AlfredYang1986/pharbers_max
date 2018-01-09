package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.stm._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.routing.BroadcastPool

import scala.collection.immutable.Map
import com.pharbers.alCalcMemory.aldata.alStorage
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog

import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.alMSA.alMaxSlaves.alGroupDataSlave
import com.pharbers.panel.util.phWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.groupMsg._
import com.pharbers.aqll.alCalcHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alCalcMemory.aljobs.alJobs.common_jobs
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import com.pharbers.aqll.alCalcHelp.alModel.java.IntegratedData
import com.pharbers.aqll.alCalcHelp.alShareData
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole

/**
  * Created by alfredyang on 12/07/2017.
  *     Modify by clock on 2017.12.19
  */
trait alGroupDataTrait { this : Actor =>
    val group_router = createGroupRouter
    val group_jobs = Ref(List[alMaxRunning]())
    //TODO shijian chuan can
    val group_schdule = context.system.scheduler.schedule(1 second, 1 second, self, groupSchedule())

    def createGroupRouter = context.actorOf(
            ClusterRouterPool(BroadcastPool(1),
                ClusterRouterPoolSettings(
                    totalInstances = 1,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitgroupslave")
                )
            ).props(alGroupDataSlave.props), name = "group-integrate-router")

    def pushGroupJobs(item: alMaxRunning) = {
        atomic { implicit thx =>
            group_jobs() = group_jobs() :+ item
        }
    }

    //TODO ask shenyong
    def canGroupJob: Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitgroupslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0
    }

    def groupScheduleJobs = {
        if (canGroupJob) {
            atomic { implicit thx =>
                val tmp = group_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    group_jobs() = group_jobs().tail
                    doGroupData(tmp.head)
                }
            }
        }
    }

    def doGroupData(item: alMaxRunning) {
        val cur = context.actorOf(alCameoGroupData.props(item, group_router))
        cur ! group_data_start()
    }
}

object alCameoGroupData {
    def props(item: alMaxRunning,
              slaveActor : ActorRef) = Props(new alCameoGroupData(item, slaveActor))
}

//TODO xuyao zixi kankan
class alCameoGroupData (item: alMaxRunning, slaveActor : ActorRef) extends Actor with ActorLogging {
    var tol = 0
    var sed = 0
    var cur = 0

    val timeoutMessager = context.system.scheduler.scheduleOnce(60 minute) {
        self ! group_data_timeout()
    }

    override def receive: Receive = {
        case group_data_start() => {
            tol = item.subs.length
            slaveActor ! group_data_hand()
        }

        case group_data_hand() => {
            if (sed < tol) {
                val tmp = item.subs(sed)
                sender ! group_data_start_impl(tmp)
                sed += 1
            }
        }

        case group_data_end(result, groupResult) => {
            result match {
                case true =>
                    cur += 1
                    resetSubGrouping(groupResult)
                    if (cur == tol) {
                        unionResult
                        alTempLog("group data trait => Success")
                    }
                case false => {
                    val msg = Map(
                        "type" -> "error",
                        "error" -> "cannot group data"
                    )
                    phWebSocket(groupResult.uid).post(msg)
                    alTempLog("group data trait => Failed")
                }
            }

            groupResult.parent = item.parent // transmit panel id
            shutCameo(groupPanelResult(groupResult))
        }

        case group_data_timeout() => {
            log.info("Warning! group data trait timeout")
            alTempLog("Warning! group data trait timeout")
            self ! group_data_end(false, item)
        }

        case msg: AnyRef =>
            alTempLog(s"Warning! Message not delivered. alCameoGroupData.received_msg=$msg")
            shutCameo(msg)
    }

    def shutCameo(msg : AnyRef) = {
        timeoutMessager.cancel()

        val agent = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
        agent ! msg

        log.debug("stopping group data trait cameo")
        alTempLog("stopping group data trait cameo")

        self ! PoisonPill
    }

    def unionResult = {
        val common = common_jobs()
        common.cur = Some(alStage(item.subs map (x => s"$memorySplitFile$group${x.tid}")))
        common.process = restore_grouped_data() ::
            do_calc() :: do_union() :: do_calc() ::
            do_map (alShareData.txt2IntegratedData(_)) :: do_calc() :: Nil
        common.result

        val concert = common.cur.get.storages.head.asInstanceOf[alStorage]
        val m = alStorage.groupBy (x =>
            (x.asInstanceOf[IntegratedData].getYearAndmonth, x.asInstanceOf[IntegratedData].getMinimumUnitCh)
        )(concert)

        val g = alStorage(m.values.map (x => x.data.head.toString).toList)
        g.doCalc
        val sg = alStage(g :: Nil)
        val pp = presist_data(Some(item.tid), Some("group"))
        pp.precess(sg)
    }

    def resetSubGrouping(mp : alMaxRunning) = item.subs = item.subs.filterNot(x => x.tid == mp.tid) :+ mp
}
