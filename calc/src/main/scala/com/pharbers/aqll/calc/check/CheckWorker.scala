package com.pharbers.aqll.calc.check

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.calc.adapter.SplitAdapter
import com.pharbers.aqll.calc.datacala.common._
import com.pharbers.aqll.calc.excel.core._
import com.pharbers.aqll.calc.split.SplitEventBus
import com.pharbers.aqll.calc.util.DateUtil

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Faiz on 2017/1/4.
  */

object CheckWorker {
    def props(a : ActorRef) = Props(new CheckWorker(a))

    /**
      * Double  sumValue
      * Double  Units
      * Long    date
      * String  market
      * String  miniproduct
      * Long    hospNum
      */
    case class exceluniondata(e: List[(Double, Double, Long, String, String, Long)])
}

class CheckWorker(aggregator: ActorRef) extends Actor with ActorLogging{
    val excelunion: ArrayBuffer[(Double, Double, Long, String, String, Long)] = ArrayBuffer.empty
    val subFun = aggregator ! CheckAggregator.aggsubcribe(self)
    val idle : Receive = {
//        case cparesult(target) => {
//            excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.commonObjectCondition()))
//        }
//        case cpamarketresult(target) => {
//            excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.getMarketname))
//        }
//        case pharesult(target) => {
//            excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.commonObjectCondition()))
//        }
//        case phamarketresult(target) => {
//            excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.getMarketname))
//        }
        case integratedresult(target) => {
            val datatime = DateUtil.getDateLong(target.getYearAndmonth.toString)
            excelunion.append((target.getSumValue, target.getVolumeUnit, datatime, target.getMarket1Ch, target.getMinimumUnit, target.getHospNum.longValue()))
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
