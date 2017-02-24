package com.pharbers.aqll.calc.split

import com.pharbers.aqll.calc.common.CalcTimeHelper
import com.pharbers.aqll.calc.maxmessages.{excelJobStart, _}
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
	        println("-*-*-*-*-*-*-*-")
	        println("join registerMaster")
            masters = (sender() :: masters).distinct
	        println(s"masters $masters")
        }

		case excelSplitStart(map) =>{
			val act = context.actorOf(SplitExcel.props)
			act ! excelJobStart(map)
			// TODO: 这个地方需要返回文件名，返回的格式为
            // TODO: (原始文件名, List(分解后文件名))
            // TODO: SplitJobsContainer.pushJobs(完善参数，将返回值放到这里)
            // TODO: 记得每一个Master要维护一个文件名和子文件名，在data中
		}

		case excelJobStart(map) => {
            // TODO: 这个地方需要添加一个参数就是你分拆用户数据后的列表，参数为下（同分拆文件的返回值）
            // TODO: (原始文件名, List(分解后文件名))
            // TODO: SplitJobsContainer.queryJobSubNamesWithName(完善参数，将返回值放到这里))
            // TODO: 对每一个Subname分配算能，在分配算能钱，先发送一分拆的文件
            // TODO: 以下代码为，小文件分配的算能，理论上没有大改动
//		    val act = context.actorOf(SplitMaster.props)
//		    masters = masters :+ act
//		    context.watch(act)
//			act ! startReadExcel(map)
			println("-*-*-*-*-*-*-*-")
			println("join excelJobStart")
            if (signJobs(map)) {
                tc.start
            } else {
                // TODO: 记录下来，隔一段时间分配一次jobs
            }

		}

        case requestMasterAverage(f, s, sum) => {
            val result = SplitJobsContainer.pushRequestAverage(f, s, sum)
            if (result._1) {
                masters.foreach(x => x ! responseMasterAverage(f, result._2))
            }
        }

		case excelJobEnd(filename) => {
		    println(filename)
		}
		case Terminated(a) => {
		    println("-*-*-*-*-*-*-*-")
            println(s"self = $self")

		    // job完成，提醒用户
			val end = tc.lastTimes
		    println(s"执行时间为 : ${end / 1000} 秒")
		}
		case x => println(s"x = ${x}");???
	}

    def signJobs(map : Map[String, Any]) : Boolean = {
	    signJobsAcc(map, masters)
    }

    def signJobsAcc(map : Map[String, Any], cur : List[ActorRef]) : Boolean = {

        if (cur.isEmpty) {
            println("not enough calc to do the jobs")
            false
        } else {
	        println(s"cur.head.path = ${cur.head.path}")
            implicit val t = Timeout(2 seconds)
            val f = cur.head ? new excelJobStart(map)
	        try {
		        Await.result(f.mapTo[signJobsResult], t.duration) match {
			        case c : canHandling => println("sign jobs success"); true
			        case k : masterBusy => signJobsAcc(map, cur.tail)
		        }
	        } catch {
		        case ex : Exception => println("timeout"); false
	        }
        }
    }

}

trait CreateSplitMaster { this : Actor => 
	def CreateSplitMaster = context.actorOf(SplitReception.props)
}