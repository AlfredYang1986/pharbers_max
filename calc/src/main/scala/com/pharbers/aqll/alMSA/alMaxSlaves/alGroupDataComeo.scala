package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.routing.BroadcastPool
import scala.concurrent.duration._
import scala.collection.immutable.Map
import akka.actor.SupervisorStrategy.Escalate
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.alCalcMemory.aldata.alStorage
import com.pharbers.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.groupMsg._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.{common_jobs, grouping_jobs}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}

/**
  * Created by alfredyang on 12/07/2017.
  *     Modify by clock on 2017.12.19
  */
object alGroupDataComeo {
    def props(item: alMaxRunning,
              traitComeo: ActorRef,
              counter : ActorRef) = Props(new alGroupDataComeo(item, traitComeo, counter))
    val core_number = server_info.cpu
}

class alGroupDataComeo (item: alMaxRunning,
                        traitComeo: ActorRef,
                        counter: ActorRef) extends Actor with ActorLogging {
    var cur = 0
    var sed = 0

    val impl_router = context.actorOf(
        BroadcastPool(alGroupDataComeo.core_number).props(alGroupSlaveImpl.props),
        name = "concert-group-router"
    )
    val timeoutMessager = context.system.scheduler.scheduleOnce(60 minute) {
        self ! group_data_timeout()
    }

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Escalate
    }

    override def postRestart(reason: Throwable) = {
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {
        case group_data_start_impl(_) => {
            alTempLog(s"开始 group data ==> ${item.parent}/${item.tid}")
            val cj = grouping_jobs(
                Map(
                    grouping_jobs.max_uuid -> item.parent,//split result's parent
                    grouping_jobs.group_uuid -> item.tid  //split result's subs head
                )
            )
            val result = cj.result
            val (parent, subs) = result.get.asInstanceOf[(String, List[String])]//this parent = item.tid

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

        case group_data_end(result, _) => {
            result match {
                case true =>
                    cur += 1
                    if (cur == alGroupDataComeo.core_number) {
                        unionResult
                        alTempLog("group data => Success")
                        shutSlaveCameo(group_data_end(result, item))
                    }
                case false =>
                    alTempLog("group data => Failed")
                    shutSlaveCameo(group_data_end(result, item))
            }
        }

        case group_data_timeout() => {
            log.info("Warning! group data timeout")
            alTempLog("Warning! group data timeout")
            self ! group_data_end(false, item)
        }

        case canDoRestart(reason: Throwable) => {
            super.postRestart(reason)
            alTempLog("Warning! group_data Node canDoRestart")
            self ! group_data_start_impl(item)
        }

        case cannotRestart(reason: Throwable) => {
            log.info(s"Warning! group_data Node reason is $reason")
            alTempLog(s"Warning! group_data Node cannotRestart, reason is $reason")
            self ! group_data_end(false, item)
        }

        case msg: AnyRef => alTempLog(s"Warning! Message not delivered. alGroupDataComeo.received_msg=$msg")
    }


    def shutSlaveCameo(msg : AnyRef) = {
        timeoutMessager.cancel()

        traitComeo ! msg

        log.info("stop group cameo")
        alTempLog("stop group cameo")

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
}
