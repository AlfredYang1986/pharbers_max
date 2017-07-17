package com.pharbers.aqll.alMSA.alMaxSlaves

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.actor.SupervisorStrategy.Restart
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.worker_calc_core_split_jobs
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.calc_sum_result
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_timeout
import com.pharbers.aqll.common.alFileHandler.clusterListenerConfig.singletonPaht

import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */

object alCalcDataComeo {
    def props(c : alCalcParmary, lsp : alMaxProperty, originSender : ActorRef, owner : ActorRef) =
        Props(new alCalcDataComeo(c, lsp, originSender, owner))
    val core_number = 4
}

class alCalcDataComeo (c : alCalcParmary,
                       op : alMaxProperty,
                       originSender : ActorRef,
                       owner : ActorRef) extends Actor with ActorLogging {

    var cur = 0
    var sed = 0
    var sum = 0

    var r : alMaxProperty = null

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def postRestart(reason: Throwable) : Unit = {
        super.postRestart(reason)
        // TODO : 计算次数，从新计算
        self ! calc_data_start_impl(op, c)
    }

    import alCalcDataComeo._

    override def receive: Receive = {
        case calc_data_timeout() => {
            log.debug("timeout occur")
            shutSlaveCameo(split_excel_timeout())
        }
        case calc_data_sum(sub_sum) => {
            println("comeo sum plue one")
            r.sum = r.sum ++: sub_sum

            sum += 1
            if (sum == core_number) {
                r.isSumed = true
                originSender ! calc_data_sum(r.sum)
            }
        }
        case calc_data_average(avg) => impl_router ! calc_data_average(avg)
        case c : calc_data_result => originSender ! c
        case calc_data_end(result, p) => {
            if (result) {
                cur += 1
                if (cur == core_number) {
                    println(s"return true")
                    val r = calc_data_end(true, p)
                    owner ! r
                    shutSlaveCameo(r)
                }
            } else {
                val r = calc_data_end(false, p)
                owner ! r
                shutSlaveCameo(r)
            }
        }
        case calc_data_start_impl(_, _) => {

            val core_number = 4
            val mid = UUID.randomUUID.toString
            val lst = (1 to core_number).map (x => worker_calc_core_split_jobs(Map(worker_calc_core_split_jobs.max_uuid -> op.uuid,
                                                   worker_calc_core_split_jobs.calc_uuid -> op.subs(0 * core_number + x - 1).uuid,
                                                   worker_calc_core_split_jobs.mid_uuid -> mid))).toList

            val m = lst.map (_.result.get.asInstanceOf[(String, List[String])]._2).flatten.distinct
            val q = m.map (x => alMaxProperty(mid, x, Nil))
            r = alMaxProperty(op.uuid, mid, q)

//            impl_router ! calc_data_extra(op.parent)
            impl_router ! calc_data_hand()
        }
        case calc_data_hand() => {
            if (r != null) {
                sender ! calc_data_start_impl(alMaxProperty(r.parent, r.uuid, r.subs(sed) :: Nil), c)
                sed += 1
            }
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(10 minute) {
        self ! calc_data_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("shutting calc data cameo")
        context.stop(self)
    }

    val impl_router =
        context.actorOf(BroadcastPool(core_number).props(alCalcDataImpl.props), name = "concert-calc-router")
}