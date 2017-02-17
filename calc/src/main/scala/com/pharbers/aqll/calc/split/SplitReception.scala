package com.pharbers.aqll.calc.split

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.Cluster
import com.pharbers.aqll.calc.maxmessages.{excelJobEnd, excelJobStart}
import com.pharbers.aqll.calc.maxmessages.startReadExcel
import com.pharbers.aqll.calc.excel.CPA.CpaMarket
import com.pharbers.aqll.calc.util.{GetProperties, ListQueue}
import com.typesafe.config.ConfigFactory

/**
 * enum
 */
object JobCategories {
    object cpaMarketJob extends JobDefines(0, "CpaMarket")
    object cpaProductJob extends JobDefines(1, "CpaProduct")
    object phaMarketJob extends JobDefines(2, "PhaMarket")
    object phaProductJob extends JobDefines(3, "PhaProduct")
    object integratedJob extends JobDefines(4, "Integrated")
}

sealed case class JobDefines(t : Int, des : String)

object SplitReception {
	def props = Props[SplitReception]
}

class SplitReception extends Actor with ActorLogging with CreateSplitMaster {
	import SplitReception._
	var masters = Seq[ActorRef]()     // supervisor

	var begin : Long = 0
	var end : Long = 0
  val ip = GetProperties.loadConf("cluster-listener").getString("cluster-listener.ip")
  val sendnode = GetProperties.loadConf("cluster-listener").getString("cluster-listener.sendnode")
	def receive = {
//		case excelJobStart(filename, cat, company, n) => {
		case excelJobStart(map) => {
		    val act = context.actorOf(SplitMaster.props)
		    masters = masters :+ act
		    context.watch(act)
//			act ! startReadExcel(filename, cat, company, n)
			act ! startReadExcel(map)
			begin = System.currentTimeMillis
		}
		case excelJobEnd(filename) => {
		    println(filename)
		}
		case Terminated(a) => {
		    println("-*-*-*-*-*-*-*-")
        println(s"self = $self")
		    context.unwatch(a)
		    masters = masters.filterNot (_ == a)

        context.actorSelection(ip + sendnode) ! FreeListQueue(context.actorOf(SplitReception.props), self)
		    // job完成，提醒用户
			  end = System.currentTimeMillis() - begin
		    println(s"执行时间为 : ${end / 1000} 秒")
		}
    case Registration(member) => {
        context.actorSelection(ip + sendnode) ! Registration(member)
    }
		case _ => ???
	}
}

trait CreateSplitMaster { this : Actor => 
	def CreateSplitMaster = context.actorOf(SplitReception.props)
}