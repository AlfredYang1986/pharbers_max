package com.pharbers.aqll.calc.split

import com.pharbers.aqll.calc.common.CalcTimeHelper
import com.pharbers.aqll.calc.maxmessages.{excelJobEnd, excelJobStart, registerMaster, signJobsResult, canHandling, masterBusy}
import com.pharbers.aqll.calc.maxmessages.startReadExcel
import com.pharbers.aqll.calc.excel.CPA.CpaMarket
import com.pharbers.aqll.calc.util.{GetProperties, ListQueue}
import com.typesafe.config.ConfigFactory

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.Cluster
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.Await

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
    def name = "reception-actor"
}

class SplitReception extends Actor with ActorLogging with CreateSplitMaster {
	import SplitReception._
	var masters = List[ActorRef]()     // round robin // TODO: 多线程，修改成STM的线程安全模式，交给你了

    val tc = new CalcTimeHelper(0)
	val ip = GetProperties.loadConf("cluster-listener").getString("cluster-listener.ip")
	val sendnode = GetProperties.loadConf("cluster-listener").getString("cluster-listener.sendnode")

	def receive = {
        case registerMaster() => {
            masters = (sender() :: masters).distinct
        }

		case excelJobStart(map) => {
//		    val act = context.actorOf(SplitMaster.props)
//		    masters = masters :+ act
//		    context.watch(act)
//			act ! startReadExcel(map)

            if (signJobs(map)) {
                tc.start
            } else {
                // TODO: 记录下来，隔一段时间分配一次jobs
            }

		}
		case excelJobEnd(filename) => {
		    println(filename)
		}
		case Terminated(a) => {
		    println("-*-*-*-*-*-*-*-")
            println(s"self = $self")

            /**
              * 以下代码感觉都有问题
              */

            masters = masters.filterNot (_ == a)

            context.actorSelection(ip + sendnode) ! FreeListQueue(context.actorOf(SplitReception.props), self)
		    // job完成，提醒用户
			val end = tc.lastTimes
		    println(s"执行时间为 : ${end / 1000} 秒")
		}
        case Registration(member) => {
            context.actorSelection(ip + sendnode) ! Registration(member)
        }
		case _ => ???
	}

    def signJobs(map : Map[String, Any]) : Boolean = {
        signJobsAcc(map, masters)
    }

    def signJobsAcc(map : Map[String, Any], cur : List[ActorRef]) : Boolean = {

        if (cur.isEmpty) {
            println("not enough calc to do the jobs")
            false
        } else {
            implicit val t = Timeout(2 seconds)
            val f = cur.head ? new excelJobStart(map)
            Await.result(f.mapTo[signJobsResult], t.duration) match {
                case c : canHandling => println("sign jobs success"); true
                case k : masterBusy => signJobsAcc(map, cur.tail)
            }
        }
    }

}

trait CreateSplitMaster { this : Actor => 
	def CreateSplitMaster = context.actorOf(SplitReception.props)
}