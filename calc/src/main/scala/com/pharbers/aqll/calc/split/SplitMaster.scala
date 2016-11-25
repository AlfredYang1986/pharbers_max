package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging
import com.pharbers.aqll.calc.excel.core.cpaproductinteractparser
import com.pharbers.aqll.calc.excel.CPA.CpaMarket
import com.pharbers.aqll.calc.excel.core.cpamarketinteractparser
import com.pharbers.aqll.calc.common.DefaultData.capLoadXmlPath
import akka.routing.RoundRobinPool
import akka.cluster.routing.ClusterRouterPoolSettings
import akka.routing.BroadcastPool
import akka.cluster.routing.ClusterRouterPool

object SplitMaster {
	def props = Props[SplitMaster]

    case class startReadExcel(filename : String, cat : JobDefines)
}

class SplitMaster extends Actor with ActorLogging with CreateSplitWorker {
	import SplitMaster._
	import JobCategories._
	
	val router = CreateSplitWorker
	
	val spliting : Receive = {
		case startReadExcel(filename, cat) => {
		    val parser = cat match {
		        case cpaJob => cpamarketinteractparser(capLoadXmlPath.cpamarketxmlpath_en,
		                                               capLoadXmlPath.cpamarketxmlpath_ch,
		                                               router)
		    }
	        parser.startParse(filename, 1)
	        context.become(aggregate)
		}
		case _ => ???
	}

	val aggregate : Receive = {
	    case startReadExcel(filename, cat) => println("one master only start one cal process at one time")
	    
		case obj => {
		    if(obj.isInstanceOf[CpaMarket]){
		        val cpamarket = obj.asInstanceOf[CpaMarket]
		        println(cpamarket.getCity)
		    }
		}
	}
	
	def receive = spliting
}

trait CreateSplitWorker { this : Actor =>
	def CreateSplitWorker = {
	    context.actorOf(
            ClusterRouterPool(RoundRobinPool(10), ClusterRouterPoolSettings(    
                totalInstances = 50, maxInstancesPerNode = 10,
                allowLocalRoutees = true, useRole = None)).props(Props[SplitWorker]),
              name = "worker-router")
	}
}