package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging
import com.pharbers.aqll.calc.excel.core.cpaproductinteractparser
import com.pharbers.aqll.calc.split.SplitWorker.cpamarketreadexcel
import com.pharbers.aqll.calc.excel.CPA.CpaMarket

object SplitMaster {
	case class startReadExcel(filename : String)
	def props = Props[SplitMaster]
}

class SplitMaster extends Actor with ActorLogging with CreateSplitWorker {
	import SplitMaster._
	val idle : Receive = {
		case startReadExcel(filename) => {
		    val act = context.actorOf(SplitWorker.props)
		    act ! cpamarketreadexcel(filename)
			context.become(spliting)
		}
		case _ => ???
	}

	val spliting : Receive = {
		case obj => {
		    if(obj.isInstanceOf[CpaMarket]){
		        val cpamarket = obj.asInstanceOf[CpaMarket]
		        println(cpamarket.getCity)
		    }
		}
	}
	
	def receive = idle
}

trait CreateSplitWorker { this : Actor =>
	def CreateSplitWorker = context.actorOf(Props[SplitWorker])
}