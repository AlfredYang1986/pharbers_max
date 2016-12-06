package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.actor.ActorRef
import com.pharbers.aqll.calc.excel.model.modelRunData
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.datacala.algorithm.maxSumData
import com.pharbers.aqll.calc.datacala.algorithm.maxCalcData
import com.pharbers.aqll.calc.util.DateUtil

object SplitHashMappingWorker {
	def props(a : ActorRef) = Props(new SplitHashMappingWorker(a))
	
	case class HashMappingEnd() extends broadcastingDefines
}

class SplitHashMappingWorker(aggregator : ActorRef) extends Actor with ActorLogging {
    var isSub = false
	val mr : ArrayBuffer[modelRunData] = ArrayBuffer.empty

    def receive = {
		case d : modelRunData => {
			if (isSub == false) {
				isSub = true
				aggregator ! SplitAggregator.aggsubcribe(self)
			}
			mr += d	
		}
		case SplitHashMappingWorker.HashMappingEnd() => {
			lazy val maxSum = new maxSumData()(mr.toStream).toList
			aggregator ! SplitWorker.requestaverage(maxSum)	
		}
		case SplitEventBus.average(avg) => {
	    	lazy val calc = new maxCalcData()(mr.toStream, avg)
	    	val result = calc.groupBy (x => (x.uploadYear, x.uploadMonth)) map { x =>
				    (DateUtil.getDateLong(x._1._1,x._1._2), (x._2 map(_.finalResultsValue) sum, x._2 map(_.finalResultsUnit) sum))
				}
	    	aggregator ! SplitWorker.postresult(result)
	    }
	    case _ => Unit
	}
}