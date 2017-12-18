package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alCalaHelp.alLog.alTempLog
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{canDoRestart, canIReStart, cannotRestart}
import com.pharbers.aqll.alCalcOther.alWebSocket.alWebSocket
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.pharbers.panel.pfizer.phPfizerHandle
import play.api.libs.json._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.ymMsg._

import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import scala.collection.immutable.Map

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by jeorch on 17-10-11.
  */
object alCalcYMCameo {
    def props(calcYM_job : alPanelItem,
              slaveActor : ActorRef,
              counter : ActorRef) = Props(new alCalcYMCameo(calcYM_job, slaveActor, counter))
}

class alCalcYMCameo (calcYM_job: alPanelItem,
                     slaveActor: ActorRef,
                     counter: ActorRef) extends Actor with ActorLogging {
    val timeoutMessager = context.system.scheduler.scheduleOnce(30 minute) {
        self ! calcYM_timeout()
    }

    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {
        case calcYM_start_impl(calcYM_job) => {
            val args: Map[String, List[String]] = Map(
                "company" -> List(calcYM_job.company),
                "uid" -> List(calcYM_job.uid),
                "cpas" -> calcYM_job.cpa.split("&").toList,
                "gycxs" -> calcYM_job.gycx.split("&").toList
            )
            alTempLog("开始过滤日期,arg=" + args)

            val (result, ym, mkt) = try {
                val ym = phPfizerHandle(args).calcYM.asInstanceOf[JsString].value
                val markets = phPfizerHandle(args).getMarkets.asInstanceOf[JsString].value
                alTempLog(s"calcYM result, ym = $ym, mkt = $mkt")
                (true, ym, markets)
            } catch {
                case ex: Exception =>
                    alTempLog("cannot calcYM" + ex.getMessage)
                    (false, "0"," ")
            }

            self ! calcYM_end2(result, ym, mkt)
        }

        case calcYM_end2(result, ym, mkt) => {
            result match {
               case true =>
                   val msg = Map(
                       "type" -> "calc_ym_result",
                       "ym" -> ym,
                       "mkt" -> mkt
                   )
                   alWebSocket(calcYM_job.uid).post(msg)
               case false =>
                   val msg = Map(
                       "type" -> "error",
                       "error" -> "cannot calcYM"
                   )
                   alWebSocket(calcYM_job.uid).post(msg)
            }
            slaveActor ! calcYM_end2(result, ym, mkt)
            shutSlaveCameo(calcYMResult(ym))
        }

        case calcYM_timeout() => {
            log.info("timeout occur")
            alTempLog("calc ym timeout")
            shutSlaveCameo(calcYM_timeout())
        }

        case canDoRestart(reason: Throwable) =>
            super.postRestart(reason)
            self ! calcYM_start_impl(calcYM_job)

        case cannotRestart(reason: Throwable) => {
            log.info(s"reason is ${reason}")
            self ! calcYM_end(false, "cannot calcYM")
        }

        case msg : AnyRef => alTempLog(s"Warning! Message not delivered. alCalcYMCameo.received_msg=$msg")
    }


    def shutSlaveCameo(msg : AnyRef) = {
        timeoutMessager.cancel()

        val agent = context.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
        agent ! msg

        log.info("stop calcYM cameo")
        alTempLog("stop calcYM cameo")

        self ! PoisonPill
    }
}
