package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_jobs
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.{split_excel_end, split_excel_start_impl, split_excel_timeout}

import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */

object alSplitExcelComeo {
    def props(file : String, par : alCalcParmary, originSender : ActorRef, owner : ActorRef) =
        Props(new alSplitExcelComeo(file, par, originSender, owner))
}

class alSplitExcelComeo(file : String,
                        par : alCalcParmary,
                        originSender : ActorRef,
                        owner : ActorRef) extends Actor with ActorLogging {

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def postRestart(reason: Throwable) : Unit = {
        super.postRestart(reason)
        // TODO : 计算次数，从新计算
        self ! split_excel_start_impl(file, par)
    }

    override def receive: Receive = {
        case split_excel_timeout() => {
            log.debug("timeout occur")
            shutSlaveCameo(split_excel_timeout())
        }
        case result : split_excel_end => {
            owner forward result
            shutSlaveCameo(result)
        }
        case split_excel_start_impl(f, c) => {
            val result = max_jobs(file).result
            try {
                val (p, sb) = result.map (x => x).getOrElse(throw new Exception("cal error"))
                c.uuid = p.toString
                sender ! split_excel_end(true, p.toString, sb.asInstanceOf[List[String]], c)

            } catch {
                case _ : Exception => sender ! split_excel_end(false, "", Nil, c)
            }
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(10 minute) {
        self ! split_excel_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping split excel cameo")
        context.stop(self)
    }
}
