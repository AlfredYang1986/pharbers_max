package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxProperty
import com.pharbers.aqll.alCalc.almain.alShareData
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.{common_jobs, grouping_jobs}
import com.pharbers.aqll.alCalcMemory.alprecess.alprecessdefines.alPrecessDefines._
import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.{group_data_end, group_data_hand, group_data_start_impl, group_data_timeout}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_timeout

import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */

object alGroupDataComeo {
    def props(mp : alMaxProperty, originSender : ActorRef, owner : ActorRef) =
        Props(new alGroupDataComeo(mp, originSender, owner))
    val core_number = 4
}

class alGroupDataComeo (mp : alMaxProperty,
                        originSender : ActorRef,
                        owner : ActorRef) extends Actor with ActorLogging {

    var cur = 0
    var sed = 0

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def postRestart(reason: Throwable) : Unit = {
        super.postRestart(reason)
        // TODO : 计算次数，从新计算
        self ! group_data_start_impl(mp)
    }

    import alGroupDataComeo._

    override def receive: Receive = {
        case group_data_timeout() => {
            log.debug("timeout occur")
            shutSlaveCameo(split_excel_timeout())
        }
        case group_data_end(result, p) => {
            if (result) {
                cur += 1
                if (cur == core_number) {

                    unionResult

                    val r = group_data_end(true, mp)
                    owner ! r
                    shutSlaveCameo(r)
                }
            } else {
                val r = group_data_end(false, mp)
                owner ! r
                shutSlaveCameo(r)
            }
        }
        case group_data_start_impl(_) => {
            val cj = grouping_jobs(Map(grouping_jobs.max_uuid -> mp.parent, grouping_jobs.group_uuid -> mp.uuid))
            val result = cj.result
            val (p, sb) = result.get.asInstanceOf[(String, List[String])]
            mp.subs = sb.map (x => alMaxProperty(p, x, Nil))

            impl_router ! group_data_hand()
        }
        case group_data_hand() => {
            val tmp = alMaxProperty(mp.uuid, mp.subs(sed).uuid, Nil)
            sender ! group_data_start_impl(tmp)
            sed += 1
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(10 minute) {
        self ! group_data_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("grouping data cameo")
        context.stop(self)
    }

    def unionResult = {
        val common = common_jobs()
        common.cur = Some(alStage(mp.subs map (x => "config/group/" + x.uuid)))
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
        val pp = presist_data(Some(mp.uuid), Some("group"))
        pp.precess(sg)
    }

    val impl_router =
        context.actorOf(BroadcastPool(core_number).props(alGroupSlaveImpl.props), name = "concert-group-router")
}
