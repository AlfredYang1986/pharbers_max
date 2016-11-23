package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging

object SplitWorker {
	
}

class SplitWorker extends Actor with ActorLogging with CreateSplitWorker {
	import SplitWorker._
	val idle : Receive = {
		case _ => Unit
	}
	
	val working : Receive = {
		case _ => Unit
	}

	def receive = idle
}