package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alCalcMemory.aljobs.alJob.max_jobs
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.{split_excel_end, split_excel_start_impl, split_excel_timeout}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._

import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */

object alSplitExcelComeo {
    def props(file : String, par : alCalcParmary, originSender : ActorRef, owner : ActorRef, counter : ActorRef) =
        Props(new alSplitExcelComeo(file, par, originSender, owner, counter))
}

class alSplitExcelComeo(file : String,
                        par : alCalcParmary,
                        originSender : ActorRef,
                        owner : ActorRef,
                        counter : ActorRef) extends Actor with ActorLogging {

    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
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

            /**
              * Modified by Jeorch on 02/08/2017.
              * 制造一个错误，检验错误计数，重算流程
            println("start push error!")
            throw new Exception("&&& ==> Some alSplitExcelComeo Error！")
              */

            try {
                val (p, sb) = result.map (x => x).getOrElse(throw new Exception("cal error"))
                c.uuid = p.toString
                sender ! split_excel_end(true, p.toString, sb.asInstanceOf[List[String]], c)

            } catch {
                case _ : Exception => sender ! split_excel_end(false, "", Nil, c)
            }
        }

        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! split_excel_start_impl(file, par)

        case cannotRestart(reason: Throwable) => {
            new alMessageProxy().sendMsg("100", "username", Map("error" -> s"error with actor=${self}, reason=${reason}"))
//            println(s"&&&&&& 重启3次后，依然未能正确执行 => error with actor=${self}, reason=${reason} &&&&&&")
            self ! split_excel_end(false,"",Nil,null)
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
