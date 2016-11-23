package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging

object SplitReception {
	def props = Props[SplitReception]
	
	case class excelJobStart(filename : String)
	case class excelJobEnd(filename : String)
}

class SplitReception extends Actor with ActorLogging with CreateSplitMaster {
	import SplitReception._
	def receive = {
		case excelJobStart(filename) => {
				
		}
		case excelJobEnd(filename) => {
			
		}
		case _ => ???
	}
}

trait CreateSplitMaster { this : Actor => 
	def CreateSplitMaster = context.actorOf(SplitReception.props)
}