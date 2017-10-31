package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{canDoRestart, canIReStart, cannotRestart}
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.{alRestoreColl, alRestoreColl2}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoRestoreBson.{restore_bson_end, restore_bson_start_impl, restore_bson_timeout}
import scala.concurrent.duration._

/**
  * Created by jeorch on 17-10-30.
  */
object alRestoreBsonComeo {
    def props(coll : String,
              sub_uuid : String,
              originSender : ActorRef,
              owner : ActorRef,
              counter : ActorRef) = Props(new alRestoreBsonComeo(coll, sub_uuid, originSender, owner, counter))
}

class alRestoreBsonComeo (val coll : String,
                          val sub_uuid : String,
                          val originSender : ActorRef,
                          val owner : ActorRef,
                          val counter : ActorRef) extends Actor with ActorLogging {
    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {

        case restore_bson_start_impl(coll, sub_uuid) => {
            alRestoreColl2().apply(s"${coll}", sub_uuid :: Nil)
            self ! restore_bson_end(true, sub_uuid)
        }
        case restore_bson_end(result, sub_uuid) => {
            owner forward restore_bson_end(result, sub_uuid)
            shutSlaveCameo(restore_bson_end(result, sub_uuid))
        }
        case restore_bson_timeout() => {
            log.debug("timeout occur")
            shutSlaveCameo(restore_bson_timeout())
        }

        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! restore_bson_start_impl(coll, sub_uuid)

        case cannotRestart(reason: Throwable) => {
            new alMessageProxy().sendMsg("cannot restore bson", sub_uuid, Map("type" -> "txt"))
            log.info(s"reason is ${reason}")
            self ! restore_bson_end(false, sub_uuid)
        }

        case msg : AnyRef => log.info(s"Warning! Message not delivered. alRestoreBsonCameo.received_msg=${msg}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(30 minute) {
        self ! restore_bson_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.info("stopping restore bson cameo")
        timeoutMessager.cancel()
        //        context.stop(self)
        self ! PoisonPill
    }

}
