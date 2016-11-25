package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging
import com.pharbers.aqll.calc.excel.core.cpamarketinteractparser

object SplitWorker {
	def props = Props[SplitWorker]
	
	case class cpamarketreadexcel(filename: String)
	
	object loadXmlPath {
	    lazy val cpamarketxmlpath_ch = """config/consumer/CpaMarketDataStruct.xml"""
	    lazy val cpamarketxmlpath_en = """config/consumer/FieldNamesCpaMarketDataStruct.xml"""
	}
}

class SplitWorker extends Actor with ActorLogging with CreateSplitWorker {
    // TODO :  我忘记 是在这里的 working里面做放大拼接   还是在外面spliting做放大拼接
	import SplitWorker._
	val idle : Receive = {
	    case cpamarketreadexcel(filename) => {
	        val cpamarket = cpamarketinteractparser(loadXmlPath.cpamarketxmlpath_en,loadXmlPath.cpamarketxmlpath_ch,sender())
	        cpamarket.startParse(filename, 1)
	    }
	    case _ => {
	        println("其他")
	    }
	}
	
	val working : Receive = {
		case _ => Unit
	}

	def receive = idle
}