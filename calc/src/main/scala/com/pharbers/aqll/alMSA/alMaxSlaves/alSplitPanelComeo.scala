package com.pharbers.aqll.alMSA.alMaxSlaves

import scala.concurrent.duration._
import scala.collection.immutable.Map
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.alCalcHelp.alWebSocket.alWebSocket
import com.pharbers.aqll.alCalcHelp.alMaxDefines.alMaxRunning
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.reStartMsg._
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg.splitPanelMsg._
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.common.alFileHandler.fileConfig.{fileBase, outPut}
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.{max_jobs, max_split_csv_jobs}

/**
  * Created by alfredyang on 12/07/2017.
  *     Modify by clock on 2017.12.19
  */
object alSplitPanelComeo {
    def props(item: alMaxRunning,
              counter: ActorRef) = Props(new alSplitPanelComeo(item, counter))
}

class alSplitPanelComeo(item: alMaxRunning, counter: ActorRef) extends Actor with ActorLogging {
    //TODO shijian chuancan
    val timeoutMessager = context.system.scheduler.scheduleOnce(10 minute) {
        self ! split_panel_timeout()
    }

    override def postRestart(reason: Throwable) = {
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {
        case split_panel_start_impl(item) => {
            val company = phRedisDriver().commonDriver.hget(item.uid, "company").map(x=>x).getOrElse(throw new Exception("not found company"))
            val file = fileBase + company + outPut + item.tid

            //方便测试
            val r = if(file.endsWith(".xlsx")){
                alTempLog("开始 split excel file ==> " + file)
                max_jobs(file).result
            } else {
                alTempLog("开始 split csv file ==> " + file)
                max_split_csv_jobs(file).result
            }

            val (result, parent, subs) = try {
                val (p,s) = r.map (x => x).getOrElse(throw new Exception("cal error"))
                (true, p.asInstanceOf[String],s.asInstanceOf[List[String]])
            } catch {
                case ex : Exception =>
                    alTempLog("Warning! cannot calcYM" + ex.getMessage)
                    (false, "", Nil)
            }

            self ! split_panel_end(result, item, parent, subs)
        }

        case split_panel_end(result, item, parent, subs) => {
            result match {
                case true => alTempLog("split panel file => Success")
                case false => {
                    val msg = Map(
                        "type" -> "error",
                        "error" -> "cannot split panel"
                    )
                    alWebSocket(item.uid).post(msg)
                    alTempLog("split panel file => Failed")
                }
            }
            shutSlaveCameo(splitPanelResult(item, parent, subs))
        }

        case split_panel_timeout() => {
            log.info("Warning! split panel timeout")
            alTempLog("Warning! split panel timeout")
            self ! split_panel_end(false, item, "", Nil)
        }

        case canDoRestart(reason: Throwable) =>
            super.postRestart(reason)
            alTempLog("Warning! split_panel Node canDoRestart")
            self ! split_panel_start_impl(item)

        case cannotRestart(reason: Throwable) => {
            log.info(s"Warning! split_panel Node reason is $reason")
            alTempLog(s"Warning! split_panel Node cannotRestart, reason is $reason")
            self ! split_panel_end(false, item, " ", Nil)
        }

        case msg: AnyRef => alTempLog(s"Warning! Message not delivered. alSplitPanelComeo.received_msg=$msg")
    }


    def shutSlaveCameo(msg : AnyRef) = {
        timeoutMessager.cancel()

        val agent = context.actorSelection("akka.tcp://calc@" + masterIP + ":2551/user/agent-reception")
        agent ! msg

        log.info("stop split panel cameo")
        alTempLog("stop split panel cameo")

        self ! PoisonPill
    }
}
