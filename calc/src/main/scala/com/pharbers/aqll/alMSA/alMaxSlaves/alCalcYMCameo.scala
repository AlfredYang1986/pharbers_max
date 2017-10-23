package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{canDoRestart, canIReStart, cannotRestart}
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcYM.{calcYM_end, calcYM_start_impl, calcYM_timeout}
import com.pharbers.aqll.alStart.alHttpFunc.alUpBeforeItem
import com.pharbers.panel.pfizer.impl.phPfizerHandleImpl
import play.api.libs.json.JsString

import scala.concurrent.duration._

/**
  * Created by jeorch on 17-10-11.
  */
object alCalcYMCameo {
    def props(calcYM_job : alUpBeforeItem,
              originSender : ActorRef,
              owner : ActorRef,
              counter : ActorRef) = Props(new alCalcYMCameo(calcYM_job, originSender, owner, counter))
}

class alCalcYMCameo (val calcYM_job : alUpBeforeItem,
                     val originSender : ActorRef,
                     val owner : ActorRef,
                     val counter : ActorRef) extends Actor with ActorLogging {

    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {

        case calcYM_start_impl(calcYM_job) => {
            val args: Map[String, List[String]] = Map(
                "company" -> List(calcYM_job.company),
                "user" -> List(calcYM_job.user),
                "cpas" -> calcYM_job.cpas.split("&").toList,
                "gycxs" -> calcYM_job.gycxs.split("&").toList
            )
            val ym = new phPfizerHandleImpl(args).calcYM.asInstanceOf[JsString].value
            alMessageProxy().sendMsg(ym, calcYM_job.user, Map("type" -> "txt"))
            self ! calcYM_end(true, ym)
        }
        case calcYM_end(result, ym) => {
            owner forward calcYM_end(result, ym)
            shutSlaveCameo(calcYM_end(result, ym))
        }
        case calcYM_timeout() => {
            log.debug("timeout occur")
            shutSlaveCameo(calcYM_timeout())
        }

        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! calcYM_start_impl(calcYM_job)

        case cannotRestart(reason: Throwable) => {
            new alMessageProxy().sendMsg("cannot calcYM", calcYM_job.user, Map("type" -> "txt"))
            log.info(s"reason is ${reason}")
            self ! calcYM_end(false, "cannot calcYM")
        }

        case msg : AnyRef => log.info(s"Warning! Message not delivered. alCalcYMCameo.received_msg=${msg}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(30 minute) {
        self ! calcYM_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.info("stopping calcYM cameo")
        timeoutMessager.cancel()
        context.stop(self)
    }
}
