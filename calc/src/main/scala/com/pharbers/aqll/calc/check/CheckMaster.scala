package com.pharbers.aqll.calc.check

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.RoundRobinPool
import com.pharbers.aqll.calc.common.DefaultData.{capLoadXmlPath, phaLoadXmlPath}
import com.pharbers.aqll.calc.excel.core.{row_cpamarketinteractparser, row_cpaproductinteractparser, row_phamarketinteractparser, row_phaproductinteractparser}
import com.pharbers.aqll.calc.maxmessages.{cancel, checkResult, end, startReadExcel}
import com.pharbers.aqll.calc.maxresult.{InserAdapter, Insert}
import com.pharbers.aqll.calc.split.{JobDefines, SplitEventBus}
import com.pharbers.aqll.calc.util.DateUtil

/**
  * Created by Faiz on 2017/1/4.
  */

object CheckMaster {
    def props(originSender : ActorRef) = Props(new CheckMaster(originSender))
    val num_count = 10
}

class CheckMaster(originSender : ActorRef) extends Actor with ActorLogging with CreateCheckWorker with CreateCheckEventBus with CreateCheckAggregator{
    val bus = CreateCheckEventBus
    val agg = CreateCheckAggregator(bus)
    val router = CreateCheckWorker(agg)

    var getcompany = ""
    var fileName = ""

    import CheckMaster._
    val check: Receive = {
//        case startReadExcel(filename, cat, company, n) => {
        case startReadExcel(map) => {
            getcompany = map.get("company").get.toString
            fileName = map.get("filename").get.toString
            context.become(checking)
            (map.get("JobDefines").get.asInstanceOf[JobDefines].t match {
                case 0 => {
                    row_cpamarketinteractparser(capLoadXmlPath.cpamarketxmlpath_en,
                        capLoadXmlPath.cpamarketxmlpath_ch,
                        router)
                }
                case 1 => {
                    row_cpaproductinteractparser(capLoadXmlPath.cpaproductxmlpath_en,
                        capLoadXmlPath.cpaproductxmlpath_ch,
                        router)
                }
                case 2 => {
                    row_phamarketinteractparser(phaLoadXmlPath.phamarketxmlpath_en,
                        phaLoadXmlPath.phamarketxmlpath_ch,
                        router)
                }
                case 3 => {
                    row_phaproductinteractparser(phaLoadXmlPath.phaproductxmlpath_en,
                        phaLoadXmlPath.phaproductxmlpath_ch,
                        router)
                }
            }).startParse(map.get("filename").get.toString, 1)
            bus.publish(SplitEventBus.excelEnded(map))
        }
        case _ => {
            println("exception")
        }
    }
    val checking : Receive = {
//        case startReadExcel(filename, cat, company, n) => println("one master only start one cal process at one time")
        case startReadExcel(map) => println("one master only start one cal process at one time")
        case CheckAggregator.excelResult(exd) => {
            println(s"last end originSender = $originSender")
            val time = DateUtil.getIntegralStartTime(new Date()).getTime
            new Insert().maxFactResultInsert(exd)(new InserAdapter().apply(fileName, getcompany, time))
            originSender ! checkResult("is ok")
        }
        case cancel() => {
            println(s"cancel() $self")
        }
        case end() => {
            println(s"end() $self")
        }
        case _ => Unit
    }

    def receive = check
}

trait CreateCheckWorker { this: Actor =>
    def CreateCheckWorker(a : ActorRef) = {
        context.actorOf(RoundRobinPool(10).props(CheckWorker.props(a)), name = "check-router")
    }
}

trait CreateCheckEventBus { this : Actor =>
    def CreateCheckEventBus = new SplitEventBus(CheckMaster.num_count)
}

trait CreateCheckAggregator { this : Actor =>
    def CreateCheckAggregator(b : SplitEventBus) = {
        val a = context.actorOf(CheckAggregator.props(b, self))
        context.watch(a)
        a
    }
}
