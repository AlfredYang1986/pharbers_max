package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging

import com.pharbers.aqll.calc.excel.core.cparesult
import com.pharbers.aqll.calc.excel.core.cpamarketresult

object SplitWorker {
	def props = Props[SplitWorker]
}

class SplitWorker extends Actor with ActorLogging with CreateSplitWorker {
	import SplitWorker._
	val idle : Receive = {
	    case cparesult(target) => {
	        println(s"result is : $target in context router: $self")	        
	    }
	    case cpamarketresult(target) => {
	        println(s"result is : $target in context router: $self")	        
	    }
	    case _ => {
	        println(s"result is : other in context router: $self")	        
	    }
	}
	
	val working : Receive = {
		case _ => Unit
	}

	def receive = idle
}