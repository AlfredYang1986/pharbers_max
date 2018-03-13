package com.pharbers.aqll.alMSA.alMaxSlaves

import java.util.UUID
import alCalcDataComeo._
import akka.routing.BroadcastPool
import scala.concurrent.duration._
import scala.collection.immutable.Map
import akka.actor.SupervisorStrategy.Escalate
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alCalcHelp.alMaxDefines._
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alCalcHelp.alWebSocket.phWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}

/**
  * Created by alfredyang on 12/07/2017.
  *     Modify by clock on 2017.12.21
  */
object alCalcDataComeo {
    def props(item: alMaxRunning, originSender : ActorRef) = Props(new alCalcDataComeo(item, originSender))
    val core_number: Int = server_info.cpu
}

class alCalcDataComeo (item : alMaxRunning, counter : ActorRef) extends Actor with ActorLogging {
    val impl_router = context.actorOf(
            BroadcastPool(core_number).props(alCalcDataImpl.props),
            name = "concert-calc-router"
        )

    //TODO shijian chuancan
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
            impl_router ! calc_data_hand()
            alTempLog(s"C3. ${item.parent} start segment")
        }

        case calc_data_hand() => {
            val rd = phRedisDriver().commonDriver
            var segmentSum = rd.get("segmentSum:"+item.tid).getOrElse("0").toInt
            alTempLog(s"C3.$segmentSum router start segment")
            sender ! calc_data_start_impl3(item.subs(segmentSum), item)
            segmentSum += 1
            rd.set("segmentSum:"+item.tid, segmentSum)
            if(segmentSum == core_number){
                shutSlaveCameo
            }
        }

        case calc_data_average_pre(avg_path) => {
            impl_router ! calc_data_average_one(avg_path, item.tid)
        }

        case calc_data_average_one(avg_path, bsonpath) =>  {
            val rd = phRedisDriver().commonDriver
            var avgSum = rd.get("avgSum:"+item.tid).getOrElse("0").toInt
            alTempLog(s"C5.$avgSum Calc start write bson")
            sender ! calc_data_average_post(item.subs(avgSum), item.parent, avg_path, bsonpath)
            avgSum += 1
            rd.set("avgSum:"+item.tid, avgSum)
        }

        case calc_data_timeout() => {
            log.info("Warning! calc data timeout")
            alTempLog("Warning! calc data timeout")
            shutComeoAndSendAgent(calcDataResult(false, item.uid, item.parent, 0, 0))
        }

        case canDoRestart(reason: Throwable) => {
            super.postRestart(reason)
            alTempLog("Warning! calc_data Node canDoRestart")
            self ! calc_data_start_impl(item)
        }
        
        case cannotRestart(reason: Throwable) => {
            log.info(s"Warning! calc_data Node reason is $reason")
            alTempLog(s"Warning! calc_data Node cannotRestart, reason is $reason")
            shutComeoAndSendAgent(calcDataResult(false, item.uid, item.parent, 0, 0))
        }

        case msg: AnyRef => alTempLog(s"Warning! Message not delivered. alCalcDataComeo.received_msg=$msg")
    }

    def shutSlaveCameo = {
        timeoutMessager.cancel()

        log.info("stop calc data cameo")
        alTempLog("stop calc data cameo")

        self ! PoisonPill
    }

    def shutComeoAndSendAgent(msg: AnyRef) = {
        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! msg
        shutSlaveCameo
    }
}