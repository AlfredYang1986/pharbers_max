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
import com.pharbers.aqll.alcalc.alcmd.pyshell.pyShell
import com.pharbers.aqll.calc.Http.{QueueActor, ThreadQueue}

import scala.concurrent.Await
import com.pharbers.aqll.calc.maxmessages._
import com.pharbers.aqll.calc.split.JobCategories.{integratedJob, _}
import com.pharbers.aqll.calc.split.{ClusterEventListener, SplitMaster, SplitReception, SplitReceptionSingleton}
import com.pharbers.aqll.calc.util.{GetProperties, ListQueue}
import com.pharbers.aqll.calc.check.CheckReception
import spray.json.DefaultJsonProtocol
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
/**
  * Created by Faiz on 2017/1/7.
  */


class OrderServiceApi(system: ActorSystem, timeout: Timeout) extends OrderService {
	implicit val requestTimeout = timeout

	implicit def executionContext = system.dispatcher
}

case class Item(filename: String, company: String, hospmatchpath: String)
case class Item1(company: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
	implicit val itemFormat = jsonFormat3(Item)
	implicit val itemFormat1 = jsonFormat1(Item1)
}

trait OrderService extends Directives with JsonSupport {
	implicit def executionContext: ExecutionContext

	implicit def requestTimeout: Timeout

	val routes = getCheck ~ getCalc ~ Test ~ Test2 ~ getRcommit ~ getCleanData

	def Test = get {
		path("Test") {///Users/qianpeng/Desktop/CPA_GYCX_panel_160111INF.xlsx
			val map = Map("filename" -> """config/test/BMS客户上传/CPA_GYCX_panel_160111INF.xlsx""",
				"hospdatapath" -> """20000家pfizer医院数据库表.xlsx""",
				"JobDefines" -> integratedJob,
				"company" -> "BMS",
				"calcvariable" -> 0)
			val system = CheckGloble.system
			val reception = system.actorSelection(GetProperties.singletonPaht)
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
				val map = Map("filename" -> (GetProperties  + item.filename),
					"hospmatchpath" -> (item.company + "/" + item.hospmatchpath),
					"JobDefines" -> integratedJob,
					"company" -> item.company,
					"calcvariable" -> 0)
				val system = ActorSystem(item.filename)
				val act = system.actorOf(CheckReception.props)
				val r = act ? checkExcelJobStart(map)
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
				println(s"join calc ${(GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + item.filename)}")
				val map = Map("filename" -> (GetProperties.loadConf("File.conf").getString("SCP.Upload_File_Path").toString + item.filename),
					"hospdatapath" -> "null",
					"JobDefines" -> integratedJob,
					"company" -> item.company,
					"calcvariable" -> 0)
				val system = CheckGloble.system
				val reception = system.actorSelection(GetProperties.singletonPaht)
				println(s"reception = $reception")
				reception ! excelSplitStart(map)
				complete("""{"result":"Ok"}""")
			}
		}
	}

	def getRcommit = post {
		path("commit") {
			entity(as[Item1]) { item =>
				println(s"item=${item}")
				complete("""{"result":"Ok"}""")
			}
		}
	}

	def getCleanData = post {
		path("cleandata") {
			entity(as[Item1]) { item =>
				println(s"company=${item.company}")
				val result = pyShell(item.company).excute
				val gson : Gson = new Gson()
				println(s"result=${gson.toJson(result)}")
				complete("""{"result":"""+gson.toJson(result)+"""}""")
			}
		}
	}
}
