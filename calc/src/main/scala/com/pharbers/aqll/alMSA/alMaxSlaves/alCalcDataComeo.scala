package com.pharbers.aqll.alMSA.alMaxSlaves

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines._
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData._
import akka.actor.SupervisorStrategy.Escalate
import com.pharbers.aqll.alCalaHelp.alWebSocket.alWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.splitPanelMsg.split_panel_timeout
import com.pharbers.driver.redis.phRedisDriver

import scala.collection.immutable.Map
import scala.concurrent.duration._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import alCalcDataComeo._
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg._

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by alfredyang on 12/07/2017.
  *     Modify by clock on 2017.12.20
  */

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
    var segment: List[String] = Nil
    var r: alMaxRunning = _

    val impl_router = context.actorOf(
            BroadcastPool(core_number).props(alCalcDataImpl.props),
            name = "concert-calc-router"
        )

    val timeoutMessager = context.system.scheduler.scheduleOnce(6000 minute) {
        self ! calc_data_timeout()
    }

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Escalate
    }
    
    override def postRestart(reason: Throwable) = {
        counter ! canIReStart(reason)
    }
    
    override def receive: Receive = {
        case calc_data_start_impl(_) => {
            r = item
            impl_router ! calc_data_hand()
            alTempLog("C3. router start segment")
        }

        case calc_data_hand() => {
            if (r != null) {
                alTempLog(s"C3.$sed router start segment => Success")
                sender ! calc_data_start_impl3(r.subs(sed), r)
                sed += 1
            }
        }

        case calc_data_timeout() => {
            log.info("timeout occur")
            shutSlaveCameo(split_panel_timeout())
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

            val a = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
            a ! calc_data_result(item.uid, item.tid, 0, 0, false)
            shutSlaveCameo(s"cannotRestart.reason=${reason.getMessage}")
        }
        case msg : Any => log.info(s"Error msg=[${msg}] was not delivered.in actor=${self}")
    }

    def shutSlaveCameo(msg: AnyRef) = {
        log.info(s"shutting calc data slave cameo msg=${msg}")
        timeoutMessager.cancel()
        self ! PoisonPill
    }

}