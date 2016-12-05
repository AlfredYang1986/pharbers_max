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

object SplitWorker {
	def props(a : ActorRef) = Props(new SplitWorker(a))
	
	case class requestaverage(sum: List[(String, (Double, Double, Double))])
	case class postresult(mr: Map[Long, (Double, Double)])
}

object adminData {
    lazy val hospbasedata = DefaultData.hospdatabase.toStream
	lazy val hospmatchdata = DefaultData.hospmatchdata.toStream
	lazy val market = DefaultData.marketdata.toStream
}

class SplitWorker(aggregator: ActorRef) extends Actor with ActorLogging with CreateSplitWorker {
//class SplitWorker(aggregator: ActorRef, bus : SplitEventBus) extends Actor with ActorLogging with CreateSplitWorker {
    var isSub = false
    
    val data : ArrayBuffer[integratedData] = ArrayBuffer.empty
    var mr : Stream[modelRunData] = Stream.Empty
    
	val idle : Receive = {
	    case cparesult(target) => {
//	        println(s"result is : $target in context router: $self")	        
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
//	        println(s"result is : $target in context router: $self")
	    }
	    case phamarketresult(target) => {
//	        println(s"result is : $target in context router: $self")
	    }
	    
	    case SplitEventBus.excelEnded() => {
	        if(data.size != 0) {
                val baseMaxData = BaseMaxDataArgs(new AdminHospDataBaseArgs(adminData.hospbasedata), new IntegratedDataArgs(data.toStream))
                val maxAllData = msg_MaxData(baseMaxData)
                MarketModule.dispatchMessage(maxAllData) match {
                    case None => None
                    
                    case Some(ModelRunDataArgs(modelrun)) => {
                    	data.clear
                    	mr = modelrun
                    }
                    
                    case _ => Unit
                }
            }
	        
	        lazy val maxSum = new maxSumData()(mr).toList
	        aggregator ! SplitWorker.requestaverage(maxSum)
	    }
	    case SplitEventBus.average(avg) => {
	    	/**
	    	 * 1. 通过avg1，avg2 继续本线程中的数据进行计算
	    	 * 2. 将结果发给发给aggregator
	    	 */
	    	lazy val calc = new maxCalcData()(mr, avg)
	    	val result = calc.groupBy ( x => (x.uploadYear,x.uploadMonth) ) map { x =>
				    (DateUtil.getDateLong(x._1._1,x._1._2), (x._2 map(_.finalResultsValue) sum, x._2 map(_.finalResultsUnit) sum))
				}
	    	aggregator ! SplitWorker.postresult(result)
	    }
	    case _ => {
//	        println(s"result is : other in context router: $self")
	    }
	}
	
	val working : Receive = {
	    case cpamarketresult(target) => {}
	    
	    case _ => ???
	}

	def receive = idle
}