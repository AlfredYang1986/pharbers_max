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
import scala.collection.mutable.Set

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

object dataByList {
//    var temp : Stream[modelRunData] = null
//    def integratedDataByList(oneData : Stream[modelRunData])(pre : Stream[modelRunData]) : Stream[modelRunData] = {
//        temp = pre ++: oneData
//        temp
//    }
    
    val integratedDataByList: Set[Stream[modelRunData]] = Set()
}

class SplitWorker(aggregator: ActorRef, bus : SplitEventBus) extends Actor with ActorLogging with CreateSplitWorker {
    var isSub = false
    
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
	                if(igda.size != 0) {
	                    val baseMaxData = BaseMaxDataArgs(new AdminHospDataBaseArgs(adminData.hospbasedata), new IntegratedDataArgs(igda))
	                    val maxAllData = msg_MaxData(baseMaxData)
	                    MarketModule.dispatchMessage(maxAllData) match {
	                        case None => None
	                        
	                        case Some(ModelRunDataArgs(modelrun)) => {
	                            dataByList.integratedDataByList += modelrun
	                        }
	                        
	                        case _ => Unit
	                    }
	                }
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
	    	aggregator ! SplitWorker.requestaverage(8, 9, 10)
	    }
	    case SplitEventBus.average(avg1, avg2) => {			// 对应白纸上的算法，avg1 = sum1 / sum2, avg2 = sum1 / sum3
	    	println(s"actor : $self receive average $avg1 and $avg2")
	    	/**
	    	 * 1. 通过avg1，avg2 继续本线程中的数据进行计算
	    	 * 2. 将结果发给发给aggregator
	    	 */
	    	val unit = 100.0
	    	val value = 1000.0
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