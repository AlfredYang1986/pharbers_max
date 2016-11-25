package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging
import com.pharbers.aqll.calc.split.SplitMaster.startReadExcel
import akka.actor.ActorRef
import akka.actor.Terminated

/**
 * enum
 */
object JobCategories {
    object cpaJob extends JobDefines(0, "CPA")
}

sealed case class JobDefines(t : Int, des : String)

object SplitReception {
	def props = Props[SplitReception]

	/**
	 * messages
	 */
	case class excelJobStart(filename : String, cat : JobDefines)
	case class excelJobEnd(filename : String)
	
}

class SplitReception extends Actor with ActorLogging with CreateSplitMaster {
	import SplitReception._
	var masters = Seq[ActorRef]()     // supervisor
	
	def receive = {
		case excelJobStart(filename, cat) => {
		    val act = context.actorOf(SplitMaster.props)
		    masters = masters :+ act
		    context.watch(act)
			act ! startReadExcel(filename, cat)
		}
		case excelJobEnd(filename) => {
			// TODO : 拼接成计算表后 从这里 进如开始计算吗 ？ // 说了那么多还没听懂 // 动脑
		}
		case Terminated(a) => {
		    context.unwatch(a)
		    masters = masters.filterNot (_ == a)
		    // job没有完成，提醒用户
		}
		case _ => ???
	}
}

trait CreateSplitMaster { this : Actor => 
	def CreateSplitMaster = context.actorOf(SplitReception.props)
}