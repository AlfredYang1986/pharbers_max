package com.pharbers.aqll.calc.check

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.calc.adapter.SplitAdapter
import com.pharbers.aqll.calc.datacala.common._
import com.pharbers.aqll.calc.excel.core.{cpamarketresult, cparesult, phamarketresult, pharesult}
import com.pharbers.aqll.calc.split.SplitEventBus

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Faiz on 2017/1/4.
  */

object CheckWorker {
    def props(a : ActorRef) = Props(new CheckWorker(a))

    case class exceluniondata(e: List[(Double, Double, Long, String)])
}

class CheckWorker(aggregator: ActorRef) extends Actor with ActorLogging{
    val excelunion: ArrayBuffer[(Double, Double, Long, String)] = ArrayBuffer.empty
    val subFun = aggregator ! CheckAggregator.aggsubcribe(self)
    val idle : Receive = {
        case cparesult(target) => {
            val listCpaProdcut = (target :: Nil)
            excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.commonObjectCondition()))
        }
        case cpamarketresult(target) => {
            val listCpaMarket = (target :: Nil)
            excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.getMarketname))
        }
        case pharesult(target) => {
            val listPhaProdcut = (target :: Nil)
            excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.commonObjectCondition()))
        }
        case phamarketresult(target) => {
            val listPhaMarket = (target :: Nil)
            excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.getMarketname))
        }
        case SplitEventBus.excelEnded(n) =>  {
            println(s"read ended at $self")
            aggregator ! CheckWorker.exceluniondata(excelunion.toList)
        }
        case _ => Unit
    }
    def receive = idle
    def cancelActor = {
        context.stop(self)
    }
}
