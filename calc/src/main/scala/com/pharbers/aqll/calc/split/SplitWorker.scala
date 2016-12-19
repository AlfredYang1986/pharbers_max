package com.pharbers.aqll.calc.split

import scala.concurrent.stm._
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.datacala.common._
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
import com.pharbers.aqll.calc.datacala.module.MaxMessage.msg_IntegratedData
import com.pharbers.aqll.calc.datacala.module.MaxModule
import com.pharbers.aqll.calc.adapter.SplitAdapter

object SplitWorker {
	def props(a : ActorRef) = Props(new SplitWorker(a))
	
	case class requestaverage(sum: List[(String, (Double, Double, Double))])
	case class postresult(mr: Map[Long, (Double, Double)])
	
	case class integratedataresult(integrated : Map[(Int, Int, String), List[integratedData]])
//	case class integratedataresult(integrated : ((Int, Int, String), List[integratedData]))
	case class integratedataended()
}

object adminData {
    lazy val hospbasedata = DefaultData.hospdatabase.toStream
	lazy val hospmatchdata = DefaultData.hospmatchdata.toStream
	lazy val market = DefaultData.marketdata.toStream
	lazy val product = DefaultData.productdata.toStream
}

class SplitWorker(aggregator: ActorRef) extends Actor with ActorLogging with CreateSplitWorker {
    val data : ArrayBuffer[integratedData] = ArrayBuffer.empty
    val subFun = aggregator ! SplitAggregator.aggsubcribe(self)
    
	val idle : Receive = {
	    case cparesult(target) => {
	        val listCpaProdcut = (target :: Nil).toStream
	        val integratedDataArgs = new BaseArgs((new AdminProductDataArgs(adminData.product), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserProductDataArgs(listCpaProdcut))) 
	        data ++= new splitdata(new SplitAdapter(), integratedDataArgs).d
	    }
	    case cpamarketresult(target) => {
	        val listCpaMarket = (target :: Nil).toStream
	        val integratedDataArgs = new BaseArgs((new AdminMarkeDataArgs(adminData.market), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserMarketDataArgs(listCpaMarket)))
	        data ++= new splitdata(new SplitAdapter(), integratedDataArgs).d
	    }
	    case pharesult(target) => {
            val listPhaProdcut = (target :: Nil).toStream
            val integratedDataArgs = new BaseArgs((new AdminProductDataArgs(adminData.product), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserPhaProductDataArgs(listPhaProdcut)))
	        data ++= new splitdata(new SplitAdapter(), integratedDataArgs).d
	    }
	    case phamarketresult(target) => {
	        val listPhaMarket = (target :: Nil).toStream
	        val integratedDataArgs = new BaseArgs((new AdminMarkeDataArgs(adminData.market), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserPhaMarketDataArgs(listPhaMarket)))
	        data ++= new splitdata(new SplitAdapter(), integratedDataArgs).d
	    }
	    case SplitEventBus.excelEnded() =>  {
	    	println(s"read ended at $self")
	   
	    	val tmp = data.toList.groupBy (x => (x.getUploadYear, x.getUploadMonth, x.getMinimumUnitCh))
			aggregator ! SplitWorker.integratedataresult(tmp)
	    	
//	    	(data.toList.groupBy (x => (x.getUploadYear, x.getUploadMonth, x.getMinimumUnitCh))).map { tmp => 
//		    	aggregator ! SplitWorker.integratedataresult(tmp)
//	    	}
//	    
			aggregator ! SplitWorker.integratedataended()
	    }
	    case _ => Unit
	}
	
	val working : Receive = {
	    case cpamarketresult(target) => {}
	    
	    case _ => ???
	}

	def receive = idle
	
	def cancelActor = {
		context.stop(self)
	}
}