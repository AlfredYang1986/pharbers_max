package com.pharbers.aqll.calc.split

import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import com.pharbers.aqll.calc.excel.model.integratedData
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.excel.model.integratedData
import com.pharbers.aqll.calc.excel.model.modelRunData
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.datacala.algorithm.maxCalcData
import com.pharbers.aqll.calc.datacala.algorithm.maxSumData
import com.pharbers.aqll.calc.util.DateUtil
import com.pharbers.aqll.calc.datacala.common.BaseMaxDataArgs
import com.pharbers.aqll.calc.datacala.common.AdminHospDataBaseArgs
import com.pharbers.aqll.calc.datacala.common.IntegratedDataArgs
import com.pharbers.aqll.calc.datacala.module.MaxMessage.msg_MaxData
import com.pharbers.aqll.calc.datacala.module.MaxModule
import com.pharbers.aqll.calc.datacala.common.ModelRunDataArgs

object SplitGroupMaster {
	def props(a : ActorRef) = Props(new SplitGroupMaster(a))
	
	case class groupintegrated(lst : List[integratedData])
	case class mappingend() extends broadcastingDefines
}

class SplitGroupMaster(aggregator : ActorRef) extends Actor with ActorLogging {

	val inte_lst : ArrayBuffer[integratedData] = ArrayBuffer.empty
  	val r : ArrayBuffer[modelRunData] = ArrayBuffer.empty
  	val subFun = aggregator ! SplitAggregator.aggmapsubscrbe(self)
  
	def receive = {
//		case SplitGroupMaster.groupintegrated(lst) => {
		case SplitAggregator.msg_container(group, lst) => {
	        inte_lst ++= lst
		}
		case SplitMaxBroadcasting.mappingiteratorhashed(mrd) => {
			sender() ! SplitMaxBroadcasting.mappingiteratornext()
			iteratorMrd(mrd)
		}
		case SplitMaxBroadcasting.mappingiterator(mrd) => iteratorMrd(mrd)
		case SplitMaxBroadcasting.mappingeof() => startContextAvg
		case SplitGroupMaster.mappingend() => startContextAvg
		case SplitEventBus.average(avg) => {
	    	lazy val calc = new maxCalcData()(r.toList, avg)
	    	val result = calc.groupBy (x => (x.uploadYear, x.uploadMonth)) map { x =>
				    (DateUtil.getDateLong(x._1._1, x._1._2), (x._2 map(_.finalResultsValue) sum, x._2 map(_.finalResultsUnit) sum))
				}
	    	aggregator ! SplitWorker.postresult(result)
	    }
		case _ => Unit
	}
	
	def iteratorMrd(mrd : modelRunData) = {
		inte_lst.find { iter =>
        	mrd.uploadYear == iter.uploadYear && mrd.uploadMonth == iter.uploadMonth && mrd.minimumUnitCh == iter.minimumUnitCh && mrd.hospId == iter.hospNum
        }.map { y => 
        	mrd.sumValue = y.sumValue
			mrd.volumeUnit = y.volumeUnit
			r.append(mrd)
        }.getOrElse(Unit)
	}
	
	def startContextAvg = {
		println(s"mapping size is ${r.size} in context $self")
		lazy val maxSum = new maxSumData()(r.toStream).toList
		aggregator ! SplitWorker.requestaverage(maxSum)	
	}
}