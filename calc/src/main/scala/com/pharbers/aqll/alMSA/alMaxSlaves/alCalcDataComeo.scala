package com.pharbers.aqll.alMSA.alMaxSlaves

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import akka.actor.SupervisorStrategy.Restart
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alMaxProperty
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.{calc_data_end, calc_data_hand, calc_data_start_impl, calc_data_timeout}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_timeout

import scala.concurrent.duration._

/**
  * Created by alfredyang on 12/07/2017.
  */

object alCalcDataComeo {
    def props(mp : alMaxProperty, originSender : ActorRef, owner : ActorRef) =
        Props(new alCalcDataComeo(mp, originSender, owner))
    val core_number = 4
}

class alCalcDataComeo (mp : alMaxProperty,
                       originSender : ActorRef,
                       owner : ActorRef) extends Actor with ActorLogging {

    var cur = 0
    var sed = 0

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _ => Restart
    }

    override def postRestart(reason: Throwable) : Unit = {
        super.postRestart(reason)
        // TODO : 计算次数，从新计算
        self ! calc_data_start_impl(mp)
    }

    import alCalcDataComeo._

    override def receive: Receive = {
        case calc_data_timeout() => {
            log.debug("timeout occur")
            shutSlaveCameo(split_excel_timeout())
        }
        case calc_data_end(result, p) => {
            println("ajsfdkljasdklfjalsdfalsdfjklas;")
            if (result) {
                cur += 1
                if (cur == core_number) {
                    val r = calc_data_end(true, mp)
                    owner ! r
                    shutSlaveCameo(r)
                }
            } else {
                val r = calc_data_end(false, mp)
                owner ! r
                shutSlaveCameo(r)
            }
        }
        case calc_data_start_impl(mp) => {
            println(mp)
            Thread.sleep(500)
            for(_ <- 1 to core_number)
                self ! calc_data_end(true, mp)
        }
        case calc_data_hand() => {

            sed += 1
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(10 minute) {
        self ! calc_data_timeout()
    }

    def shutSlaveCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("shutting calc data cameo")
        context.stop(self)
    }

//    val impl_router =
//        context.actorOf(BroadcastPool(core_number).props(alCalcDataImpl.props), name = "concert-calc-router")
}