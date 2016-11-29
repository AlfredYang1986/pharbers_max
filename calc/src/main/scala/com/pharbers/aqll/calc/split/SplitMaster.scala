package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging
import com.pharbers.aqll.calc.excel.core._
import com.pharbers.aqll.calc.excel.CPA.CpaMarket
import akka.routing.RoundRobinPool
import akka.cluster.routing.ClusterRouterPoolSettings
import akka.routing.BroadcastPool
import akka.cluster.routing.ClusterRouterPool
import com.pharbers.aqll.calc.common.DefaultData.{capLoadXmlPath,phaLoadXmlPath}
import com.pharbers.aqll.calc.maxmessages.startReadExcel
import com.pharbers.aqll.calc.excel.model.integratedData
import com.pharbers.aqll.calc.maxmessages.end
import scala.concurrent.stm._
import akka.actor.ActorRef

object SplitMaster {
	def props = Props[SplitMaster]
	
}

class SplitMaster extends Actor with ActorLogging with CreateSplitWorker {
	import SplitMaster._
	import JobCategories._
	val router = CreateSplitWorker
	
	val spliting : Receive = {
		case startReadExcel(filename, cat) => {
		    val parser = cat.t match {
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
		    }
	        parser.startParse(filename, 1)
	        context.become(aggregate)
		}
		
		case end() => {
		    println(s"end() $self")
		}
		case _ => {
		    println("exception")
		}
	}

	val aggregate : Receive = {
	    case startReadExcel(filename, cat) => println("one master only start one cal process at one time")
	    
	    case _ => {
	        println("sssssssssss")
	    }
	}
	
	def receive = spliting
}

trait CreateSplitWorker { this : Actor =>
    val a = context.actorOf(SplitAggregor.props(10))
	def CreateSplitWorker = {
        context.watch(a)
	    context.actorOf(
            ClusterRouterPool(RoundRobinPool(10), ClusterRouterPoolSettings(    
                totalInstances = 50, maxInstancesPerNode = 10,
                allowLocalRoutees = true, useRole = None)).props(SplitWorker.props(self, a)),
              name = "worker-router")
	}
}