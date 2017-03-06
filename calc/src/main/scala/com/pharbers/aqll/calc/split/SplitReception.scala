package com.pharbers.aqll.calc.split

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import com.pharbers.aqll.calc.common.CalcTimeHelper
import com.pharbers.aqll.calc.maxmessages.{excelJobStart, freeMaster, groupByResults, _}
import com.pharbers.aqll.calc.excel.CPA.CpaMarket
import com.pharbers.aqll.calc.util.{GetProperties, ListQueue, ScpCopyFile}
import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props, Terminated}
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
	case class ForcRestart(msg: String) extends Exception
}

class SplitReception extends Actor with ActorLogging with CreateSplitMaster {
	import SplitReception._
	var masters = Ref(List[ActorRef]())     // round robin // TODO: 多线程，修改成STM的线程安全模式，交给你了
	var jobs = Ref(List[(String, List[String])]())

    val tc = new CalcTimeHelper(0)
	val ip = GetProperties.loadConf("cluster-listener.conf").getString("cluster-listener.ip")
	val sendnode = GetProperties.loadConf("cluster-listener.conf").getString("cluster-listener.sendnode")

	override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
		log.info(s"preRestart. Reason: $reason when handling message: $message")
		super.preRestart(reason, message)
	}

	override def postRestart(reason: Throwable): Unit = {
		log.info("postRestart")
		super.postRestart(reason)

	}

	override val supervisorStrategy =
		OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
			case _: ForcRestart => Restart
			case _: IllegalArgumentException => Stop
			case _ => Escalate

		}

	def receive = {
        case registerMaster() => {
	        println("-*-*-*-*-*-*-*-")
	        println("join registerMaster")
	        atomic { implicit thx =>
		        masters() = (sender() :: masters()).distinct
	        }
	        println(s"masters $masters")
        }
        case freeMaster(master) => {
	        master ! freeMaster(master)
        }

		case excelSplitStart(map) =>{
			val act = context.actorOf(SplitExcel.props)
			implicit val t = Timeout(10 second)
			val r = act ? excelSplitStart(map)
			val result = Await.result(r.mapTo[List[(String, List[String])]], t.duration)
			result match {
				case Nil => println("file is null or error")
				case _ =>
					SplitJobsContainer.pushJobs(result.head._1,result.head._2)
					result.head._2.foreach { x =>

						self ! excelJobStart(map, (result.head._1, x))
					}
			}
		}

		case excelJobStart(mapdata, data) => {
			val sub = SplitJobsContainer.queryJobSubNamesWithName(data._1)
			val m = mapdata.updated("filename", (data._1, sub.find(_ == data._2).get)) ++
					Map("local" -> sub.find(_ == data._2).get) ++
					Map("from" -> "")
			println(s"m = ${m}")
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
			        masters.single.get.foreach(x => x ! responseMasterAverage(f, result._2))
		        }
	        }
        }

        case groupByResults(f, s, id, company, ip, dbname) => {
	        atomic { implicit thx =>
		        SplitJobsContainer.handleProcesedDataMessage(f, s, id, company, ip, dbname)
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
	        val tmpath = cur.head.path.toString
	        val server = tmpath.substring(tmpath.lastIndexOf("@")+1, tmpath.lastIndexOf(":"))
	        val from = GetProperties loadConf("File.conf") getString("SCP.Upload_Calc_File_Path").toString
	        val m = map.updated("from", from)
	        val user = GetProperties.loadConf("File.conf").getString("SCP.Server.user")
	        val pass = GetProperties.loadConf("File.conf").getString("SCP.Server.pass")
	        new ScpCopyFile().apply(server, user, pass, m) match {
		        case false => println("SCP Copy File Exception");false
		        case _ => {
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
    }
}

trait CreateSplitMaster { this : Actor => 
	def CreateSplitMaster = context.actorOf(SplitReception.props)
}