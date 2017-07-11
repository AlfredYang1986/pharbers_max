package com.pharbers.aqll.alCalcMaster.alMasterTrait

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.BroadcastPool
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alMaxSlaves.alFilterExcelSlave

import scala.concurrent.duration._

/**
  * Created by alfredyang on 11/07/2017.
  */

trait alFilterExcelTrait { this : Actor =>
    // TODO : query instance from agent
    def createFilterExcelRouter =
        context.actorOf(BroadcastPool(1).props(alFilterExcelSlave.props), name = "filter-excel-router")

    val router = createFilterExcelRouter

    def filterExcel(file : String, par : alCalcParmary) = {
        val cur = context.actorOf(alCameoFilterExcel.props(file, par, sender, self, router))
        context.watch(cur)
        import alCameoFilterExcel._
        cur ! filter_excel_start()
    }
}

object alCameoFilterExcel {
    case class filter_excel_start()
    case class filter_excel_hand()
    case class filter_excel_start_impl(p : String, par : alCalcParmary)
    case class filter_excel_end(result : Boolean)
    case class filter_excel_timeout()

    def props(file : String,
              par : alCalcParmary,
              originSender : ActorRef,
              owner : ActorRef,
              router : ActorRef) = Props(new alCameoFilterExcel(file, par, originSender, owner, router))
}

class alCameoFilterExcel(val file : String,
                         val par : alCalcParmary,
                         val originSender : ActorRef,
                         val owner : ActorRef,
                         val router : ActorRef) extends Actor with ActorLogging {

    import alCameoFilterExcel._

    var sign = false

    override def receive: Receive = {
        case filter_excel_timeout() => {
            log.debug("timeout occur")
            shutCameo(filter_excel_timeout())
        }
        case _ : filter_excel_start => router ! filter_excel_hand()
        case filter_excel_hand() => {
            if (sign == false) {
                sender ! filter_excel_start_impl(file, par)
                sign = true
            }
        }
        case result : filter_excel_end => {
            owner forward result
            shutCameo(result)
        }
    }

    import scala.concurrent.ExecutionContext.Implicits.global
    val timeoutMessager = context.system.scheduler.scheduleOnce(10 minute) {
        self ! filter_excel_timeout()
    }

    def shutCameo(msg : AnyRef) = {
        originSender ! msg
        log.debug("stopping filter excel cameo")
        context.stop(self)
    }
}
