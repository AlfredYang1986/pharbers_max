package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging

object SplitMaster {
	case class startReadExcel(filename : String)
}

class SplitMaster extends Actor with ActorLogging with CreateSplitWorker {
	import SplitMaster._
	val idle : Receive = {
		case startReadExcel(filename) => {
			context.become(spliting)
		}
		case _ => ???
	}

	val spliting : Receive = {
		case _ => Unit	
	}
	
	def receive = idle
}

trait CreateSplitWorker { this : Actor =>
	def CreateSplitWorker = context.actorOf(Props[SplitWorker])
}