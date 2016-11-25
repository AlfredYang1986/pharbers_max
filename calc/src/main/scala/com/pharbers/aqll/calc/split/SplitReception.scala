package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging
import com.pharbers.aqll.calc.split.SplitMaster.startReadExcel

object SplitReception {
	def props = Props[SplitReception]
	
	case class excelJobStart(filename : String)
	case class excelJobEnd(filename : String)
}

class SplitReception extends Actor with ActorLogging with CreateSplitMaster {
	import SplitReception._
	def receive = {
		case excelJobStart(filename) => {
		    val act = context.actorOf(SplitMaster.props)
		    println(act)
			act ! startReadExcel(filename)
		}
		case excelJobEnd(filename) => {
			// TODO : 拼接成计算表后 从这里 进如开始计算吗 ？
		}
		case _ => ???
	}
}

trait CreateSplitMaster { this : Actor => 
	def CreateSplitMaster = context.actorOf(SplitReception.props)
}