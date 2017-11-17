package com.pharbers.aqll.alMSA.alMaxSlaves

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.actor.SupervisorStrategy.Escalate
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty, endDate, startDate}
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.worker_calc_core_split_jobs
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcOther.alMessgae.{alWebSocket}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_timeout
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import akka.actor.SupervisorStrategy.{Escalate, Restart}

import scala.collection.immutable.Map
import scala.concurrent.stm.{Ref, atomic}
import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */

//trait alCalcAtomicTrait { this: Actor =>
//}

object alCalcDataComeo {
    def props(c : alCalcParmary, lsp : alMaxProperty, originSender : ActorRef, owner : ActorRef, counter : ActorRef) =
        Props(new alCalcDataComeo(c, lsp, originSender, owner, counter))
    val core_number: Int = server_info.cpu
}

class alCalcDataComeo (c : alCalcParmary,
                       op : alMaxProperty,
                       originSender : ActorRef,
                       owner : ActorRef,
                       counter : ActorRef) extends Actor with ActorLogging {
    
    var cur = 0
    var sed = 0
    var sum = 0
    import alCalcDataComeo._
    var r : alMaxProperty = null
    
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Escalate
    }
    
    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
    }
    
    override def receive: Receive = {
        case calc_data_timeout() => {
            log.info("timeout occur")
            shutSlaveCameo(split_excel_timeout())
        }
        case calc_data_sum(sub_sum) => {
            r.sum = r.sum ++: sub_sum
            sum += 1
            if (sum == core_number) {
                r.isSumed = true
                originSender ! calc_data_sum(r.sum)
            }
        }
        case calc_data_sum2(path) => {
            log.info("&& T8 STRAT &&")
            val t8 = startDate()
            println("&& T8 && alCalcDataComeo.calc_data_sum2")
            // TODO: 现在单机单线程情况，暂时不需要写多机器多线
            r.isSumed = true
            sum += 1
            if (sum == core_number) {
                r.isSumed = true
                originSender ! calc_data_sum2(path)
            }
            endDate("&& T8 && ", t8)
            log.info("&& T8 END &&")
        }
        case calc_data_average(avg) => impl_router ! calc_data_average(avg)
        case calc_data_average2(avg_path) => impl_router ! calc_data_average2(avg_path)

        case calc_data_result(v, u) => originSender ! calc_data_result(v, u)
        case calc_data_end(result, p) => {
            log.info("&& T11 START &&")
            val t11 = startDate()
            println("&& T11 && alCalcDataComeo.calc_data_end")
            if (result) {
                cur += 1
                if (cur == core_number) {
                    val r = calc_data_end(true, p)
                    owner ! r
                    shutSlaveCameo(r)
                }
            } else {
                val r = calc_data_end(false, p)
                owner ! r
                shutSlaveCameo(r)
            }
            endDate("&& T11 && ", t11)
            log.info("&& T11 END &&")
        }
        case calc_data_start_impl(_, _) => {
            log.info("&& T5 START &&")
            val t5 = startDate()
            println("&& T5 && alCalcDataComeo.calc_data_start_impl")
            val core_number = server_info.cpu
            val mid = UUID.randomUUID.toString
            val lst = (1 to core_number).map (x => worker_calc_core_split_jobs(Map(worker_calc_core_split_jobs.max_uuid -> op.uuid,
                worker_calc_core_split_jobs.calc_uuid -> op.subs(0 * core_number + x - 1).uuid,
                worker_calc_core_split_jobs.mid_uuid -> mid))).toList
            
            val m = lst.map (_.result.get.asInstanceOf[(String, List[String])]._2).flatten.distinct
            val q = m.map (x => alMaxProperty(mid, x, Nil))
            r = alMaxProperty(op.uuid, mid, q)
            
            impl_router ! calc_data_hand()
            endDate("&& T5 && ", t5)
            log.info("&& T5 END &&")
        }
        case calc_data_hand() => {
            log.info("&& T6 START &&")
            val t6 = startDate()
            println("&& T6 && alCalcDataComeo.calc_data_hand")
            if (r != null) {
                sender ! calc_data_start_impl(alMaxProperty(r.parent, r.uuid, r.subs(sed) :: Nil), c)
                sed += 1
                endDate("&& T6 && ", t6)
            }
            log.info("&& T6 END &&")
        }
        
        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! calc_data_start_impl(op, c)
        
        case cannotRestart(reason: Throwable) => {
            val msg = Map(
                "type" -> "error",
                "error" -> s"error with actor=${self}, reason=${reason}"
            )
            alWebSocket(c.uid).post(msg)

            self ! calc_data_end(false, r)
        }
        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }
    
    import scala.concurrent.ExecutionContext.Implicits.global
//    val insert_db_schedule = context.system.scheduler.schedule(5 second, 2 second, self, insertDbSchedule())
    val timeoutMessager = context.system.scheduler.scheduleOnce(6000 minute) {
        self ! calc_data_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.info("shutting calc data cameo")
        timeoutMessager.cancel()
//        insert_db_schedule.cancel()
        context.stop(self)
    }
    
    val impl_router =
        context.actorOf(BroadcastPool(core_number).props(alCalcDataImpl.props), name = "concert-calc-router")
}