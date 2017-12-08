package com.pharbers.aqll.alMSA.alMaxSlaves

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}
import akka.actor.SupervisorStrategy.Escalate
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines._
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.worker_calc_core_split_jobs
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcOther.alMessgae.alWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitPanel.split_panel_timeout
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import com.pharbers.aqll.alCalc.almain.alSegmentGroup
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.sumCalcJob
import com.pharbers.aqll.common.alFileHandler.fileConfig.{calc, memorySplitFile}
import com.pharbers.driver.redis.phRedisDriver

import scala.collection.immutable.Map
import scala.concurrent.stm.{Ref, atomic}
import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */

//trait alCalcAtomicTrait { this: Actor =>
//}

object alCalcDataComeo {
    def props(item: alMaxRunning, originSender : ActorRef, owner : ActorRef, counter : ActorRef) =
        Props(new alCalcDataComeo(item, originSender, owner, counter))
    val core_number: Int = server_info.cpu
}

class alCalcDataComeo (item : alMaxRunning,
                       originSender : ActorRef,
                       owner : ActorRef,
                       counter : ActorRef) extends Actor with ActorLogging {
    
    var cur = 0
    var sed = 0
    var segment : List[String] = Nil
    import alCalcDataComeo._
    var r : alMaxRunning = null
    
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
            shutSlaveCameo(split_panel_timeout())
        }

        case calc_data_start_impl(_) => {
            log.info("&& T5 START &&")
            val t5 = startDate()
            println("&& T5 && alCalcDataComeo.calc_data_start_impl")
            r = item
            impl_router ! calc_data_hand()
            endDate("&& T5 && ", t5)
            log.info("&& T5 END &&")
        }
        case calc_data_hand() => {
            log.info("&& T6 START &&")
            val t6 = startDate()
            println("&& T6 && alCalcDataComeo.calc_data_hand")
            if (r != null) {
                sender ! calc_data_start_impl3(r.subs(sed), r)
                sed += 1
                endDate("&& T6 && ", t6)
            }
            log.info("&& T6 END &&")
        }

        case calc_data_average_pre(avg_path) =>  {
            val redisDriver = phRedisDriver().commonDriver
            val bsonpath = UUID.randomUUID().toString
            redisDriver.lpush(s"bsonPathUid${item.uid}", bsonpath)
            impl_router ! calc_data_average_one(avg_path, bsonpath)
        }
        case calc_data_average_one(avg_path, bsonpath) =>  {
            sender ! calc_data_average_post(item.subs(sed), avg_path, bsonpath)
            sed += 1
        }

        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! calc_data_start_impl(item)
        
        case cannotRestart(reason: Throwable) => {
            val msg = Map(
                "type" -> "error",
                "error" -> s"error with actor=${self}, reason=${reason}"
            )
            alWebSocket(item.uid).post(msg)

            val a = context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/agent-reception")
            a ! calc_data_result(item.uid, item.tid, 0, 0, false)
            shutSlaveCameo(s"cannotRestart.reason=${reason.getMessage}")
        }
        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }
    
    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(6000 minute) {
        self ! calc_data_timeout()
    }
    def shutSlaveCameo(msg : AnyRef) = {
        log.info(s"shutting calc data slave cameo msg=${msg}")
        timeoutMessager.cancel()
        self ! PoisonPill
    }
    val impl_router =
        context.actorOf(BroadcastPool(core_number).props(alCalcDataImpl.props), name = "concert-calc-router")

}