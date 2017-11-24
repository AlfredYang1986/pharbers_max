package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty, alMaxRunning}
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.common_jobs
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_start
import com.pharbers.aqll.alMSA.alMaxSlaves.alGroupDataSlave
import com.pharbers.alCalcMemory.aldata.alStorage
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alMSA.alCalcAgent.alPropertyAgent.queryIdleNodeInstanceInSystemWithRole
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.groupSchedule
import scala.concurrent.Await
import scala.concurrent.stm._
import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */
trait alGroupDataTrait { this : Actor =>
    import scala.concurrent.ExecutionContext.Implicits.global

    val group_router = createGroupRouter
    val group_jobs = Ref(List[(alMaxRunning, ActorRef)]())
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
    def pushGroupJobs(item: alMaxRunning, s: ActorRef) = {
        atomic { implicit thx =>
            group_jobs() = group_jobs() :+ (item, s)
        }
    }
    def canSchduleGroupJob: Boolean = {
        implicit val t = Timeout(2 seconds)
        val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
        val f = a ? queryIdleNodeInstanceInSystemWithRole("splitgroupslave")
        Await.result(f, t.duration).asInstanceOf[Int] > 0        // TODO：现在只有一个，以后由配置文件修改
    }
    def schduleGroupJob = {
        if (canSchduleGroupJob) {
            atomic { implicit thx =>
                val tmp = group_jobs.single.get
                if (tmp.isEmpty) Unit
                else {
                    doGroupData(tmp.head._1, tmp.head._2)
                    group_jobs() = group_jobs().tail
                }
            }
        }
    }
    def doGroupData(item: alMaxRunning, s: ActorRef) {
        val cur = context.actorOf(alCameoGroupData.props(item, s, self, group_router))
        cur ! group_data_start()
    }
}

object alCameoGroupData {
    case class group_data_start()
    case class group_data_hand()
    case class group_data_start_impl(item : alMaxRunning)
    case class group_data_end(item : alMaxRunning)
    case class group_data_timeout()
    case class group_data_error(reason: Throwable)

    def props(item: alMaxRunning,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoGroupData(item, originSender, owner, router))
}

class alCameoGroupData (item: alMaxRunning,
                        originSender: ActorRef,
                        owner: ActorRef,
                        router: ActorRef) extends Actor with ActorLogging {
    import alCameoGroupData._

    var sed = 0
    var cur = 0
    var tol = 0

    override def receive: Receive = {
        case group_data_timeout() => {
            log.debug("timeout occur")
            shutCameo(group_data_timeout())
        }

        case _ : group_data_start => {
            tol = item.subs.length
            println("tol = " + tol)
            router ! group_data_hand()
        }

        case group_data_hand() => {
            if (sed < tol) {
                val tmp = item.subs(sed)
                println("alCameoGroupData group_data_start_impl")
                sender ! group_data_start_impl(tmp)
                sed += 1
            }
        }

        case group_data_end(item) => {
            if (item.result) {
                cur += 1

                resetSubGrouping(item)

                if (cur == tol) {
                    unionResult

                    val r = group_data_end(item)
                    shutCameo(r)
                }
            } else {
                val r = group_data_end(item)
                shutCameo(r)
            }
        }

        case group_data_error(reason) => {
            originSender ! group_data_error(reason)
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val group_timer = context.system.scheduler.scheduleOnce(60 minute) {
        self ! group_data_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping group data cameo")
        group_timer.cancel()
        context.stop(self)
    }

    def unionResult = {
        import com.pharbers.aqll.common.alFileHandler.fileConfig._
        val common = common_jobs()

        common.cur = Some(alStage(item.subs map (x => s"${memorySplitFile}${group}${x.tid}")))

        common.process = restore_grouped_data() ::
            do_calc() :: do_union() :: do_calc() ::
            do_map (alShareData.txt2IntegratedData(_)) :: do_calc() :: Nil
        common.result

        val concert = common.cur.get.storages.head.asInstanceOf[alStorage]
        val m = alStorage.groupBy (x =>
            (x.asInstanceOf[IntegratedData].getYearAndmonth, x.asInstanceOf[IntegratedData].getMinimumUnitCh)
        )(concert)

        val g = alStorage(m.values.map (x => x.asInstanceOf[alStorage].data.head.toString).toList)
        g.doCalc
        val sg = alStage(g :: Nil)
        val pp = presist_data(Some(item.tid), Some("group"))
        pp.precess(sg)
    }

    def resetSubGrouping(mp : alMaxRunning) =
        item.subs = item.subs.filterNot(x => x.tid == mp.tid) :+ mp
}
