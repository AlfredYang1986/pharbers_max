package com.pharbers.aqll.calc.split

import com.pharbers.aqll.calc.common.CalcTimeHelper
import com.pharbers.aqll.calc.maxmessages.{excelJobStart, freeMaster, _}
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
import scala.concurrent.stm.{Ref, atomic}

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
	var masters = Ref(List[ActorRef]())     // round robin // TODO: 多线程，修改成STM的线程安全模式，交给你了
	var jobs = Ref(List[(String, List[String])]())

    val tc = new CalcTimeHelper(0)
	val ip = GetProperties.loadConf("cluster-listener").getString("cluster-listener.ip")
	val sendnode = GetProperties.loadConf("cluster-listener").getString("cluster-listener.sendnode")

	def receive = {
        case registerMaster() => {
	        println("-*-*-*-*-*-*-*-")
	        println("join registerMaster")
	        atomic { implicit thx =>
		        masters() = (sender() :: masters()).distinct
	        }
	        println(s"masters $masters")
        }

		case excelSplitStart(map) =>{
			val act = context.actorOf(SplitExcel.props)
			implicit val t = Timeout(10 second)
			val r = act ? excelSplitStart(map)
			val result = Await.result(r.mapTo[List[(String, List[String])]], t.duration)
			result match {
				case Nil => println("file is null or error")
				case _ =>
					println(result)
					SplitJobsContainer.pushJobs(result.head._1,result.head._2)
					result.head._2.foreach { x =>
						self ! excelJobStart(map, (result.head._1, x))
					}
					//self ! excelJobStart(map, result.head)
			}
		}

		case excelJobStart(mapdata, data) => {
			val subfile = SplitJobsContainer.queryJobSubNamesWithName(data._1)
			val m = mapdata.map(x => x._1 match { case "filename" => ("filename", (data._1, subfile.find(_ == data._2).get)) case _ => x}).toMap
			println(s"m = $m")
			println(s"subfile = $subfile")
			println("-*-*-*-*-*-*-*-")
			println("join excelJobStart")
            if (signJobs(m)) {
                tc.start
            } else {
                // TODO: 记录下来，隔一段时间分配一次jobs
            }

		}

        case requestMasterAverage(f, s, sum) => {
	        atomic { implicit thx =>
		        val result = SplitJobsContainer.pushRequestAverage(f, s, sum)
		        if (result._1) {
			        println(s"masters = $masters")
			        masters.single.get.foreach(x => x ! responseMasterAverage(f, result._2))
		        }
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
	    atomic { implicit thx =>
		    signJobsAcc(map, masters.single.get)
	    }
    }

    def signJobsAcc(map : Map[String, Any], cur : List[ActorRef]) : Boolean = {

        if (cur.isEmpty) {
            println("not enough calc to do the jobs")
            false
        } else {
            implicit val t = Timeout(2 seconds)
            val f = cur.head ? new startReadExcel(map)
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