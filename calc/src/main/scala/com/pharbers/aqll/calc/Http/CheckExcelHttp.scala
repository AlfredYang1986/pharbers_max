package com.pharbers.aqll.calc.Http

import java.io.File

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import akka.pattern.ask
import com.pharbers.aqll.calc.Http.{QueueActor, ThreadQueue}

import scala.concurrent.Await
import com.pharbers.aqll.calc.maxmessages.{excelJobStart, excelSplitStart, registerMaster}
import com.pharbers.aqll.calc.split.JobCategories.{integratedJob, _}
import com.pharbers.aqll.calc.split.{ClusterEventListener, SplitMaster, SplitReception, SplitReceptionSingleton}
import com.pharbers.aqll.calc.util.{GetProperties, ListQueue}
import com.pharbers.aqll.calc.check.CheckReception
import spray.json.DefaultJsonProtocol


/**
  * Created by Faiz on 2017/1/7.
  */


class OrderServiceApi(system: ActorSystem, timeout: Timeout) extends OrderService {
	implicit val requestTimeout = timeout

	implicit def executionContext = system.dispatcher
}

case class Item(filename: String, company: String, hospdatapath: String, filetype: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
	implicit val itemFormat = jsonFormat4(Item)
}

trait OrderService extends Directives with JsonSupport {
	implicit def executionContext: ExecutionContext

	implicit def requestTimeout: Timeout

	val routes = getCheck ~ getCalc ~ Test ~ Test2

	def Test = get {
		path("Test") {///Users/qianpeng/Desktop/CPA_GYCX_panel_160111INF.xlsx
			val map = Map("filename" -> """config/test/BMS客户上传/CPA_GYCX_panel_160111INF.xlsx""",
				"hospdatapath" -> """20000家pfizer医院数据库表.xlsx""",
				"JobDefines" -> integratedJob,
				"company" -> "BMS",
				"calcvariable" -> 0)
			val system = CheckGloble.system
			val reception = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/splitreception")
			println(s"reception = $reception")
			reception ! excelSplitStart(map)
			complete("""jsonpCallback1({"result":"Ok"})""")
		}
	}

	def Test2 = post {
		path("Test2") {
			entity(as[Item]) { item =>
				println(item.filename)
				complete("""{"result":"Ok"}""")
			}
		}
	}

	def getCheck = post {
		path("checkExcel") {
			entity(as[Item]) { item =>
				val map = Map("filename" -> (GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + item.filename),
					"hospdatapath" -> """20000家pfizer医院数据库表.xlsx""",
					"JobDefines" -> integratedJob,
					"company" -> item.company,
					"calcvariable" -> 0)
				val system = ActorSystem(item.filename)
				val act = system.actorOf(CheckReception.props)
				val r = item.filetype match {
					//                    case "0" => {
					//                        act ? excelJobStart(, cpaProductJob, company, 0)
					//                    }
					//                    case "1" => {
					//                        act ? excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, cpaMarketJob, company, 0)
					//                    }
					//                    case "2" => {
					//                        act ? excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, phaProductJob, company, 0)
					//                    }
					//                    case "3" => {
					//                        act ? excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, phaMarketJob, company, 0)
					//                    }
					case "4" => {
						act ? excelJobStart(map)
					}
				}
				val result = Await.result(r.mapTo[String], requestTimeout.duration)
				if (result.equals("is ok")) {
					system.terminate()
					System.gc()
					complete("""{"result":"Ok"}""")
				} else {
					system.terminate()
					System.gc()
					complete("""{"result":"No"}""")
				}
				//                complete("""{"result":"Ok"}""")
			}

		}
	}

	def getCalc = post {
		path("calc") {
			entity(as[Item]) { item =>
				val map = Map("filename" -> (GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + item.filename),
					"hospdatapath" -> (item.company + "/" + item.hospdatapath),
					"JobDefines" -> integratedJob,
					"company" -> item.company,
					"calcvariable" -> 0)
//				val system = ActorSystem("calc")
//				val calc = system.actorOf(Props[SplitReception], "splitreception")
//				item.filetype match {
//					//                    case "0" => {
//					//                        calc ! excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, cpaProductJob, company, 0)
//					//                    }
//					//                    case "1" => {
//					//                        calc ! excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, cpaMarketJob, company, 0)
//					//                    }
//					//                    case "2" => {
//					//                        calc ! excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, phaProductJob, company, 0)
//					//                    }
//					//                    case "3" => {
//					//                        calc ! excelJobStart(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + filename, phaMarketJob, company, 0)
//					//                    }
//					case "4" => {
//						calc ! excelJobStart(map)
//					}
//				}
				val ref: AnyRef = excelJobStart(map)
				ListQueue.ListMq_Queue(ref)
				complete("""{"result":"Ok"}""")
			}
		}
	}
}
