package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{canDoRestart, canIReStart, cannotRestart}
import com.pharbers.aqll.alCalcOther.alMessgae.{alMessageProxy, alWebSocket}
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.alRestoreColl3
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoRestoreBson.{restore_bson_end, restore_bson_start_impl, restore_bson_timeout}

import scala.collection.immutable.Map
import scala.concurrent.duration._

/**
  * Created by jeorch on 17-10-30.
  */
object alRestoreBsonComeo {
    def props(coll : String,
              sub_uuids : List[String],
              originSender : ActorRef,
              owner : ActorRef,
              counter : ActorRef) = Props(new alRestoreBsonComeo(coll, sub_uuids, originSender, owner, counter))
}

class alRestoreBsonComeo (val coll : String,
                          val sub_uuids : List[String],
                          val originSender : ActorRef,
                          val owner : ActorRef,
                          val counter : ActorRef) extends Actor with ActorLogging {
    override def postRestart(reason: Throwable) : Unit = {
        // TODO : 计算次数，重新计算
        counter ! canIReStart(reason)
    }

    override def receive: Receive = {

        case restore_bson_start_impl(coll, sub_uuids) => {
            alRestoreColl3().apply(s"${coll}", sub_uuids)
            self ! restore_bson_end(true, coll)
        }
        case restore_bson_end(result, sub_uuid) => {
            owner forward restore_bson_end(result, sub_uuid)
            shutSlaveCameo(restore_bson_end(result, sub_uuid))
        }
        case restore_bson_timeout() => {
            log.info("timeout occur")
            shutSlaveCameo(restore_bson_timeout())
        }

        case canDoRestart(reason: Throwable) => super.postRestart(reason); self ! restore_bson_start_impl(coll, sub_uuids)

        case cannotRestart(reason: Throwable) => {
            val msg = Map(
                "type" -> "error",
                "error" -> "cannot restore bson"
            )
            alWebSocket(coll).post(msg)
            log.info(s"reason is ${reason}")
            self ! restore_bson_end(false, coll)
        }

        case msg : AnyRef => log.info(s"Warning! Message not delivered. alRestoreBsonCameo.received_msg=${msg}")
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(600 minute) {
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
