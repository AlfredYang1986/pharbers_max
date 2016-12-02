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

object SplitWorker {
	def props(a : ActorRef, b : SplitEventBus) = Props(new SplitWorker(a, b))
	
	case class requestaverage(sum1 : Double, sum2 : Double, sum3 : Double)
	case class postresult(value : Double, unit : Double)
}

object adminData {
    lazy val hospbasedata = DefaultData.hospdatabase.toStream
	lazy val hospmatchdata = DefaultData.hospmatchdata.toStream
	lazy val market = DefaultData.marketdata.toStream
}

class SplitWorker(aggregator: ActorRef, bus : SplitEventBus) extends Actor with ActorLogging with CreateSplitWorker {
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
                bus.subscribe(self, "AggregorBus")
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
    		/**
    		 * 1. 分组并计算sum1, sum2, sum3
    		 * 2. 向a发sum1，sum2，sum3
    		 */
	        if(data.size != 0) {
                println(s"current context handle integrate: ${data.size}")
                val baseMaxData = BaseMaxDataArgs(new AdminHospDataBaseArgs(adminData.hospbasedata), new IntegratedDataArgs(data.toStream))
                val maxAllData = msg_MaxData(baseMaxData)
                MarketModule.dispatchMessage(maxAllData) match {
                    case None => None
                    
                    case Some(ModelRunDataArgs(modelrun)) => {
                    	println(s"current handle model: ${modelrun.size}")
                    	data.clear
                    	mr = modelrun
                    	println(s"head is : ${mr.head}")
                    }
                    
                    case _ => Unit
                }
            }
	        lazy val maxSum = new maxSumData()(mr)
	        //这块儿我写错了 昨天的的计算结果应该是巧合
//	        maxSum.foreach{ x => 
//	            aggregator ! SplitWorker.requestaverage(x._2._1, x._2._2, x._2._3)
//	        }
	    }
	    case SplitEventBus.average(avg1, avg2) => {			// 对应白纸上的算法，avg1 = sum1 / sum2, avg2 = sum1 / sum3
	    	println(s"actor : $self receive average $avg1 and $avg2")
	    	/**
	    	 * 1. 通过avg1，avg2 继续本线程中的数据进行计算
	    	 * 2. 将结果发给发给aggregator
	    	 */
	    	lazy val calc = new maxCalcData()(mr, avg1, avg2) 
	        lazy val value = calc.map(_.finalResultsValue).sum
	    	lazy val unit = calc.map(_.finalResultsUnit).sum
	    	println(s"value = ${value} , unit = ${unit}")
	    	aggregator ! SplitWorker.postresult(value, unit)
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