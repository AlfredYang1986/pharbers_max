package com.pharbers.aqll.calc.split

import com.pharbers.aqll.calc.common.DefaultData.capLoadXmlPath
import com.pharbers.aqll.calc.common.DefaultData.phaLoadXmlPath
import com.pharbers.aqll.calc.excel.core.row_cpamarketinteractparser
import com.pharbers.aqll.calc.excel.core.row_cpaproductinteractparser
import com.pharbers.aqll.calc.excel.core.row_phamarketinteractparser
import com.pharbers.aqll.calc.excel.core.row_phaproductinteractparser
import com.pharbers.aqll.calc.maxmessages.end
import com.pharbers.aqll.calc.maxmessages.startReadExcel
import com.pharbers.aqll.calc.excel.model.modelRunData

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.routing.ClusterRouterPool
import akka.cluster.routing.ClusterRouterPoolSettings
import akka.routing.RoundRobinPool
import com.pharbers.aqll.calc.maxmessages.cancel
import com.pharbers.aqll.calc.maxresult.Insert
import com.pharbers.aqll.calc.maxresult.InserAdapter
import java.util.Date

import akka.routing.ConsistentHashingRouter._
import akka.routing.ConsistentHashingPool

import scala.concurrent.ExecutionContext.Implicits.global
import com.pharbers.aqll.calc.util.DateUtil

object SplitVarPramary {
    
}

object SplitMaster {
	def props = Props[SplitMaster]
	
	val num_count = 10
	

}

class SplitMaster extends Actor with ActorLogging 
	with CreateSplitWorker 
	with CreateSplitEventBus
	with CreateSplitAggregator {

	import SplitMaster._
	import JobCategories._
	
	val bus = CreateSplitEventBus
	val agg = CreateSplitAggregator(bus)
	val router = CreateSplitWorker(agg)
	var fileName = ""
	var getcompany = ""

	val ready : Receive = {
		case startReadExcel(filename, cat, company, n) => {
		    getcompany = company
		    fileName = filename//.substring(filename.lastIndexOf("""/""") + 1, filename.length())
	        context.become(spliting)
		    (cat.t match {
		        case 0 => {
		            row_cpamarketinteractparser(capLoadXmlPath.cpamarketxmlpath_en, 
		                                           capLoadXmlPath.cpamarketxmlpath_ch, 
		                                           router)
		        }
		        case 1 => {
		            row_cpaproductinteractparser(capLoadXmlPath.cpaproductxmlpath_en, 
		                                            capLoadXmlPath.cpaproductxmlpath_ch, 
		                                            router)
		        }
		        case 2 => {
		            row_phamarketinteractparser(phaLoadXmlPath.phamarketxmlpath_en, 
		                                           phaLoadXmlPath.phamarketxmlpath_ch, 
		                                           router)
		        }
		        case 3 => {
		            row_phaproductinteractparser(phaLoadXmlPath.phaproductxmlpath_en, 
		                                            phaLoadXmlPath.phaproductxmlpath_ch, 
		                                            router)
		        }
		    }).startParse(filename, 1)
		    bus.publish(SplitEventBus.excelEnded(n))
		}
		case _ => {
		    println("exception")
		}
	}

	val spliting : Receive = {
	    case startReadExcel(filename, cat, company, n) => println("one master only start one cal process at one time")
	    
	    case SplitAggregator.aggregatefinalresult(mr, aggregator) => {
	        val time = DateUtil.getIntegralStartTime(new Date()).getTime
	    	new Insert().maxResultInsert(mr)(new InserAdapter().apply(fileName, getcompany, time))
			//context.stop(aggregator)
			context.stop(self)
			System.gc()
	    }

		case cancel() => {
		    println(s"cancel() $self")
		}
		case end() => {
		    println(s"end() $self")
		}
	    
	    case _ => Unit
	}
	
	def receive = ready
}

trait CreateSplitWorker { this : Actor =>
	def CreateSplitWorker(a : ActorRef) = {
//	    context.actorOf(
//            ClusterRouterPool(RoundRobinPool(10), ClusterRouterPoolSettings(    
//                totalInstances = 10, maxInstancesPerNode = SplitMaster.num_count,
//                allowLocalRoutees = true, useRole = None)).props(SplitWorker.props(a)),
//              name = "worker-router")
		context.actorOf(RoundRobinPool(10).props(SplitWorker.props(a)), name = "worker-router")
	}
}

trait CreateSplitEventBus { this : Actor => 
	def CreateSplitEventBus = new SplitEventBus(SplitMaster.num_count)
}

trait CreateSplitAggregator { this : Actor => 
    def CreateSplitAggregator(b : SplitEventBus) = {
    	val a = context.actorOf(SplitAggregator.props(b, self))
    	context.watch(a)
    	a
    }
}