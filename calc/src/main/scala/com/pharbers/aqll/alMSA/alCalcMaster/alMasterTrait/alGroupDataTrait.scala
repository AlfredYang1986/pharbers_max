package com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.common_jobs
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_start
import com.pharbers.aqll.alMSA.alMaxSlaves.alGroupDataSlave

import scala.concurrent.stm._
import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */
trait alGroupDataTrait { this : Actor =>
    def createGroupRouter =
        context.actorOf(
            ClusterRouterPool(BroadcastPool(2),
                ClusterRouterPoolSettings(
                    totalInstances = 2,
                    maxInstancesPerNode = 1,
                    allowLocalRoutees = false,
                    useRole = Some("splitgroupslave")
                )
            ).props(alGroupDataSlave.props), name = "group-integrate-router")

    val group_router = createGroupRouter

    def pushGroupJob(property : alMaxProperty, s : ActorRef) = {
        atomic { implicit thx =>
            group_jobs() = group_jobs() :+ (property, s)
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
                    groupData(tmp.head._1, tmp.head._2)
                    group_jobs() = group_jobs().tail
                }
            }
        }
    }

    def groupData(property : alMaxProperty, s : ActorRef) {
        val cur = context.actorOf(alCameoGroupData.props(property, s, self, group_router))
        cur ! group_data_start()
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val group_schdule = context.system.scheduler.schedule(1 second, 1 second, self, group_schedule())

    val group_jobs = Ref(List[(alMaxProperty, ActorRef)]())
    case class group_schedule()
}

object alCameoGroupData {
    case class group_data_start()
    case class group_data_hand()
    case class group_data_start_impl(sub : alMaxProperty)
    case class group_data_end(result : Boolean, property : alMaxProperty)
    case class group_data_timeout()

    def props(property : alMaxProperty,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoGroupData(property, originSender, owner, router))
}

class alCameoGroupData (val property : alMaxProperty,
                        val originSender : ActorRef,
                        val owner : ActorRef,
                        val router : ActorRef) extends Actor with ActorLogging {

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
            tol = property.subs.length
            router ! group_data_hand()
        }
        case group_data_hand() => {
            if (sed < tol) {
                val tmp = property.subs(sed)
                sender ! group_data_start_impl(tmp)
                sed += 1
            }
        }
        case group_data_end(result, mp) => {
            if (result) {
                cur += 1
                resetSubGrouping(mp)

                if (cur == tol) {
                    unionResult

                    val r = group_data_end(true, property)
                    owner ! r
                    shutCameo(r)
                }
            } else {
                val r = group_data_end(false, property)
                owner ! r
                shutCameo(r)
            }
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val group_timer = context.system.scheduler.scheduleOnce(10 minute) {
        self ! group_data_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping group data cameo")
        context.stop(self)
    }

    def unionResult = {
        val common = common_jobs()
        common.cur = Some(alStage(property.subs map (x => "config/group/" + x.uuid)))
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
        val pp = presist_data(Some(property.uuid), Some("group"))
        pp.precess(sg)
    }

    def resetSubGrouping(mp : alMaxProperty) =
        property.subs = property.subs.filterNot(x => x.uuid == mp.uuid) :+ mp
}
