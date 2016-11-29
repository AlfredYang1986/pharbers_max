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

object SplitWorker {
	def props(m: ActorRef, a: ActorRef) = Props(new SplitWorker(m, a))
}

object adminData {
    lazy val hospbasedata = DefaultData.hospdatabase.toStream
	
	lazy val hospmatchdata = DefaultData.hospmatchdata.toStream
	
	lazy val market = DefaultData.marketdata.toStream
}

object dataByList {
    var temp:List[modelRunData] = Nil
    def integratedDataByList(oneData: List[modelRunData])(pre: List[modelRunData]):List[modelRunData] = {
        temp = pre ::: oneData
        temp
    }
}

class SplitWorker(master: ActorRef, aggregor: ActorRef) extends Actor with ActorLogging with CreateSplitWorker {
    var isSub = false
    
	val idle : Receive = {
	    case cparesult(target) => {
//	        println(s"result is : $target in context router: $self")	        
	    }
	    case cpamarketresult(target) => {
//	        if (!isSub) {
//                isSub = true
//                aggregor.subscribe(self, "AggregorBus")
//	        }
	        
	        val listCpaMarket = (target :: Nil).toStream
	        val integratedDataArgs = new BaseArgs((new AdminMarkeDataArgs(adminData.market), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserMarketDataArgs(listCpaMarket)))
	        val dataMsg = msg_IntegratedData(integratedDataArgs)
	        MarketModule.dispatchMessage(dataMsg) match {
	            case None => println("is Null");None
	            
	            case Some(IntegratedDataArgs(igda)) => {
	                if(igda.size != 0) {
	                    val baseMaxData = BaseMaxDataArgs(new AdminHospDataBaseArgs(adminData.hospbasedata), new IntegratedDataArgs(igda))
	                    val maxAllData = msg_MaxData(baseMaxData)
	                    MarketModule.dispatchMessage(maxAllData) match {
	                        case None => println("is null maxAllData");None
	                        
	                        case Some(ModelRunDataArgs(modelrun)) => {
	                            dataByList.temp = dataByList.integratedDataByList(modelrun.toList)(dataByList.temp)
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