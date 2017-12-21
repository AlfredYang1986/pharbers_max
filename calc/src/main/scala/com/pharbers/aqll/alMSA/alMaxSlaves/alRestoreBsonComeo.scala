package com.pharbers.aqll.alMSA.alMaxSlaves

import scala.concurrent.duration._
import scala.collection.immutable.Map
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alCalaHelp.alWebSocket.alWebSocket
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.restoreMsg._
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alCalaHelp.alFinalDataProcess.alRestoreColl

/**
  * Created by jeorch on 17-10-30.
  *     Modify by clock on 2017.12.21
  */
object alRestoreBsonComeo {
    def props(uid: String, panel: String, counter: ActorRef) = Props(new alRestoreBsonComeo(uid, panel, counter))
}

class alRestoreBsonComeo(uid: String, panel: String, counter: ActorRef) extends Actor with ActorLogging {
    //TODO shijian chuancan
    val timeoutMessager = context.system.scheduler.scheduleOnce(600 minute) {
        self ! restore_bson_timeout()
    }

    override def postRestart(reason: Throwable) = {
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {
        case restore_bson_start_impl(_, _) => {
            val rd = phRedisDriver().commonDriver
            val company = rd.hget(uid, "company").get
            val bsonpath = rd.hget(panel, "tid").get

            alRestoreColl().apply(s"$company$bsonpath", bsonpath)

            self ! restore_bson_end(true)
        }

        case restore_bson_end(result) => {
            if(result){
                alTempLog("restore bson => Success")
            }else{
                val msg = Map(
                    "type" -> "error",
                    "error" -> "cannot restore bson"
                )
                alWebSocket(uid).post(msg)
                alTempLog("restore bson => Failed")
            }
            shutSlaveCameo(restoreBsonResult(result, uid))
        }

        case restore_bson_timeout() => {
            log.info("Warning! restore bson timeout")
            alTempLog("Warning! restore bson timeout")
            self ! restore_bson_end(false)
        }

        case canDoRestart(reason: Throwable) =>
            super.postRestart(reason)
            alTempLog("Warning! restore_bson Node canDoRestart")
            self ! restore_bson_start_impl(uid, panel)

        case cannotRestart(reason: Throwable) => {
            log.info(s"Warning! restore_bson Node reason is $reason")
            alTempLog(s"Warning! restore_bson Node cannotRestart, reason is $reason")
            self ! restore_bson_end(false)
        }

        case msg : AnyRef => log.info(s"Warning! Message not delivered. alRestoreBsonCameo.received_msg=${msg}")
    }

    def shutSlaveCameo(msg: AnyRef) = {
        timeoutMessager.cancel()

        val agent = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
        agent ! msg

        log.info("stop restore bson cameo")
        alTempLog("stop restore bson cameo")

        self ! PoisonPill
    }
}
