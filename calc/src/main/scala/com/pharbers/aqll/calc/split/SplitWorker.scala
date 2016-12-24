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
import com.pharbers.aqll.calc.datacala.module.MaxMessage.msg_IntegratedData
import com.pharbers.aqll.calc.datacala.module.MaxModule
import com.pharbers.aqll.calc.adapter.SplitAdapter
import com.pharbers.aqll.calc.excel.CPA.CpaProduct
import com.pharbers.aqll.calc.excel.CPA.CpaMarket
import com.pharbers.aqll.calc.excel.PharmaTrust.PharmaTrustPorduct
import com.pharbers.aqll.calc.excel.PharmaTrust.PharmaTrustMarket

object SplitWorker {
	def props(a : ActorRef) = Props(new SplitWorker(a))
	
	case class requestaverage(sum: List[(String, (Double, Double, Double))])
	case class postresult(mr: Map[String, (Long, Double, Double, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], String)])
	
	case class integratedataresult(integrated : Map[(Int, Int, String), List[integratedData]])
	case class integratedataended()
	
	case class exceluniondata(e: List[(Double, Double, Long, String)])
}

object adminData {
    lazy val hospbasedata = DefaultData.hospdatabase
	lazy val hospmatchdata = DefaultData.hospmatchdata
	lazy val market = DefaultData.marketdata
	lazy val product = DefaultData.productdata
}

class SplitWorker(aggregator: ActorRef) extends Actor with ActorLogging with CreateSplitWorker {
    val data : ArrayBuffer[integratedData] = ArrayBuffer.empty
    
    val excelunion: ArrayBuffer[(Double, Double, Long, String)] = ArrayBuffer.empty
    val subFun = aggregator ! SplitAggregator.aggsubcribe(self)
    
    val cpaproexcel: ArrayBuffer[CpaProduct] = ArrayBuffer.empty
    val cpomarexcel: ArrayBuffer[CpaMarket] = ArrayBuffer.empty
    val phaproexcel: ArrayBuffer[PharmaTrustPorduct] = ArrayBuffer.empty
    val phamarexcel: ArrayBuffer[PharmaTrustMarket] = ArrayBuffer.empty
    
	val idle : Receive = {
	    case cparesult(target) => {
	        val listCpaProdcut = (target :: Nil)
	        excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.commonObjectCondition()))
	        val integratedDataArgs = new BaseArgs((new AdminProductDataArgs(adminData.product), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserProductDataArgs(listCpaProdcut))) 
	        data ++= new splitdata(new SplitAdapter(), integratedDataArgs).d
	    }
	    case cpamarketresult(target) => {
	        val listCpaMarket = (target :: Nil)
	        excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.getMarketname))
	        val integratedDataArgs = new BaseArgs((new AdminMarkeDataArgs(adminData.market), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserMarketDataArgs(listCpaMarket)))
	        data ++= new splitdata(new SplitAdapter(), integratedDataArgs).d
	    }
	    case pharesult(target) => {
            val listPhaProdcut = (target :: Nil)
            excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.commonObjectCondition()))
            val integratedDataArgs = new BaseArgs((new AdminProductDataArgs(adminData.product), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserPhaProductDataArgs(listPhaProdcut)))
	        data ++= new splitdata(new SplitAdapter(), integratedDataArgs).d
	    }
	    case phamarketresult(target) => {
	        val listPhaMarket = (target :: Nil)
	        excelunion.append((target.getSumValue, target.getVolumeUnit, target.getHospNum, target.getMarketname))
	        val integratedDataArgs = new BaseArgs((new AdminMarkeDataArgs(adminData.market), new AdminHospMatchDataArgs(adminData.hospmatchdata), new UserPhaMarketDataArgs(listPhaMarket)))
	        data ++= new splitdata(new SplitAdapter(), integratedDataArgs).d
	    }
	    case SplitEventBus.excelEnded() =>  {
	    	println(s"read ended at $self")
	    	
	    	aggregator ! SplitWorker.exceluniondata(excelunion.toList)
	    	
	    	val tmp = data.toList.groupBy (x => (x.getUploadYear, x.getUploadMonth, x.getMinimumUnitCh))
			aggregator ! SplitWorker.integratedataresult(tmp)
	    	
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