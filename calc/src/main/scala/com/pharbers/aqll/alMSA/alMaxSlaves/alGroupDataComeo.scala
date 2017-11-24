package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.routing.BroadcastPool
import com.pharbers.alCalcMemory.aldata.alStorage
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alMaxProperty, alMaxRunning}
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.{common_jobs, grouping_jobs}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitPanel.split_panel_timeout
import com.pharbers.driver.redis.phRedisDriver

import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */

object alGroupDataComeo {
    def props(item: alMaxRunning,
              originSender: ActorRef,
              owner: ActorRef,
              counter : ActorRef) = Props(new alGroupDataComeo(item, originSender, owner, counter))
    val core_number = server_info.cpu
}

class alGroupDataComeo (item: alMaxRunning,
                        originSender: ActorRef,
                        owner: ActorRef,
                        counter: ActorRef) extends Actor with ActorLogging {
    import alGroupDataComeo._

    var r : alMaxProperty = null
    var cur = 0
    var sed = 0

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Escalate
    }

    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {
        case group_data_timeout() => {
            log.debug("timeout occur")
            shutSlaveCameo(split_panel_timeout())
        }

        case group_data_start_impl(_) => {
            val cj = grouping_jobs(
                Map(
                    grouping_jobs.max_uuid -> item.parent,
                    grouping_jobs.group_uuid -> item.tid
                )
            )
            val result = cj.result
            val (parent, subs) = result.get.asInstanceOf[(String, List[String])]
            item.subs = subs.map{x=>
                phRedisDriver().commonDriver.sadd(parent, x)
                alMaxRunning(item.uid, x, parent)
            }
            impl_router ! group_data_hand()
        }

        case group_data_hand() => {
            val tmp = alMaxRunning(item.uid, item.subs(sed).tid, item.tid)
            sender ! group_data_start_impl(tmp)
            sed += 1
        }

        case group_data_end(result) => {
            if (result.result) {
                cur += 1
                println("cur = " + cur)
                if (cur == core_number) {
                    unionResult
                    println("unionResult")
                    val r = group_data_end(item)
                    owner ! r
                    shutSlaveCameo(r)
                }
            } else {
                println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                item.result = false
                val r = group_data_end(item)
                owner ! r
                shutSlaveCameo(r)
            }
        }

        case canDoRestart(reason: Throwable) =>
            super.postRestart(reason)
            self ! group_data_start_impl(item)

        case cannotRestart(reason: Throwable) => {
            originSender ! group_data_error(reason)
            self ! group_data_end(item)
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(60 minute) {
        self ! group_data_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("grouping data cameo")
        timeoutMessager.cancel()
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

    val impl_router = context.actorOf(
            BroadcastPool(core_number).props(alGroupSlaveImpl.props),
            name = "concert-group-router"
        )
}
