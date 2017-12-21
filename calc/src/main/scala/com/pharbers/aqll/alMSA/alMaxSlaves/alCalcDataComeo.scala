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
import com.pharbers.aqll.alCalcHelp.alWebSocket.alWebSocket
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.calcMsg._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg._
import com.pharbers.alCalcMemory.alprecess.alsplitstrategy.server_info
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
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
            alTempLog("C3. router start segment")
        }

        case calc_data_hand() => {
            val rd = phRedisDriver().commonDriver
            var sum = rd.get("sum:"+item.tid).get.toInt
            alTempLog(s"C3.$sum router start segment")
            sender ! calc_data_start_impl3(item.subs(sum), item)
            sum += 1
            rd.set("sum:"+item.tid, sum)
            if(sum == core_number){
                rd.set("sum:"+item.tid, 0)
                shutSlaveCameo
            }
        }

        case calc_data_average_pre(avg_path) => {
            impl_router ! calc_data_average_one(avg_path, item.tid)
        }

        case calc_data_average_one(avg_path, bsonpath) =>  {
            val rd = phRedisDriver().commonDriver
            var sum = rd.get("sum:"+item.tid).get.toInt
            alTempLog(s"C5.$sum Calc start write bson")
            sender ! calc_data_average_post(item.subs(sum), avg_path, bsonpath)
            sum += 1
            rd.set("sum:"+item.tid, sum)
            if(sum == core_number){
                rd.set("sum:"+item.tid, 0)
            }
        }

        case calc_data_end(result, v, u) => {
            val rd = phRedisDriver().commonDriver
            if (result) {
                var sum = rd.get("sum:"+item.tid).get.toInt
                sum += 1
                rd.set("sum:"+item.tid, sum)

                val old_value = rd.hget("calced:"+item.tid, "value").getOrElse("0").toDouble
                val old_unit = rd.hget("calced:"+item.tid, "unit").getOrElse("0").toDouble
                rd.hset("calced:"+item.tid, "value", old_value + v)
                rd.hset("calced:"+item.tid, "unit", old_unit + u)

                if(sum == core_number){
                    rd.set("sum:"+item.tid, 0)
                    alTempLog("Calc data => Success")
                    shutComeoAndSendAgent(calcDataResult(true, item.uid, item.parent))
                }
            } else {
                rd.set("sum:"+item.tid, 0)
                val msg = Map(
                    "type" -> "error",
                    "error" -> "cannot calc data"
                )
                alWebSocket(item.uid).post(msg)
                alTempLog("calc data => Failed")
                shutComeoAndSendAgent(calcDataResult(false, item.uid, item.parent))
            }
        }

        case calc_data_timeout() => {
            log.info("Warning! calc data timeout")
            alTempLog("Warning! calc data timeout")
            shutComeoAndSendAgent(calcDataResult(false, item.uid, item.parent))
        }

        case canDoRestart(reason: Throwable) => {
            super.postRestart(reason)
            alTempLog("Warning! calc_data Node canDoRestart")
            self ! calc_data_start_impl(item)
        }
        
        case cannotRestart(reason: Throwable) => {
            log.info(s"Warning! calc_data Node reason is $reason")
            alTempLog(s"Warning! calc_data Node cannotRestart, reason is $reason")
            shutComeoAndSendAgent(calcDataResult(false, item.uid, item.parent))
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