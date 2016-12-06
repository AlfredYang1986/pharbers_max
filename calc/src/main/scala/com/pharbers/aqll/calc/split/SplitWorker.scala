package com.pharbers.aqll.calc.split

import scala.concurrent.stm._
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.datacala.common._
import com.pharbers.aqll.calc.datacala.module.MarketMessage.msg_IntegratedData
import com.pharbers.aqll.calc.datacala.module.MarketMessage.msg_MaxData
import com.pharbers.aqll.calc.datacala.module.MarketModule
import com.pharbers.aqll.calc.excel.core.cpamarketresult
import com.pharbers.aqll.calc.excel.core.cparesult
import com.pharbers.aqll.calc.excel.core.phamarketresult
import com.pharbers.aqll.calc.excel.core.pharesult
import com.pharbers.aqll.calc.excel.model.integratedData
import com.pharbers.aqll.calc.excel.model.modelRunData
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.EventBus
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.datacala.algorithm.maxSumData
import com.pharbers.aqll.calc.datacala.algorithm.maxCalcData
import com.pharbers.aqll.calc.util.DateUtil
import com.pharbers.aqll.calc.datacala.algorithm.maxCalcUnionAlgorithm

object SplitWorker {
	def props(a : ActorRef, m : ActorRef) = Props(new SplitWorker(a, m))
	
	case class requestaverage(sum: List[(String, (Double, Double, Double))])
	case class postresult(mr: Map[Long, (Double, Double)])
}

object adminData {
    lazy val hospbasedata = DefaultData.hospdatabase.toStream
	lazy val hospmatchdata = DefaultData.hospmatchdata.toStream
	lazy val market = DefaultData.marketdata.toStream
}

class SplitWorker(aggregator: ActorRef, mapping : ActorRef) extends Actor with ActorLogging with CreateSplitWorker {
    var isSub = false
    val data : ArrayBuffer[integratedData] = ArrayBuffer.empty
    
	val idle : Receive = {
	    case cparesult(target) => {

	    }
	    case cpamarketresult(target) => {
	        if (!isSub) {
                isSub = true
                aggregator ! SplitAggregator.aggsubcribe(self)
	        }
	        
	        val listCpaMarket = (target :: Nil).toStream
	        val integratedDataArgs = new BaseArgs((new AdminMarkeDataArgs(adminData.market), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserMarketDataArgs(listCpaMarket)))
	        val dataMsg = msg_IntegratedData(integratedDataArgs)
	        MarketModule.dispatchMessage(dataMsg) match {
	            case None => None
	            
	            case Some(IntegratedDataArgs(igda)) => {
	            	data ++= igda
	            }
	            case _ => Unit
	        }
	        
	    }
	    case pharesult(target) => {

	    }
	    case phamarketresult(target) => {

	    }
	    case SplitEventBus.excelEnded() => {
	        if(data.size != 0) {
	        	(new maxCalcUnionAlgorithm())(data.toStream, adminData.hospbasedata)(mrd => mapping ! mrd)
            }
	        aggregator ! SplitHashMappingWorker.HashMappingEnd()
	    }
	    case _ => {

	    }
	}
	
	val working : Receive = {
	    case cpamarketresult(target) => {}
	    
	    case _ => ???
	}

	def receive = idle
}