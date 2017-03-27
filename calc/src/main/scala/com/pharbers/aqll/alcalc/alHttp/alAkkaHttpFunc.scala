package com.pharbers.aqll.alcalc.alHttp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import com.google.gson.Gson
import com.pharbers.aqll.alcalc.alcmd.pyshell.pyShell
import com.pharbers.aqll.alcalc.alfinaldataprocess.alFileExport.alFileExport
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext

/**
  * Created by qianpeng on 2017/3/26.
  */

class alAkkaHttpFuncApi(system: ActorSystem, timeout: Timeout) extends alAkkaHttpFunc {
	implicit val requestTimeout = timeout

	implicit def executionContext = system.dispatcher
}

case class alCalcItem(filename: String, company: String)
case class alCommitItem(filename: String, company: String, hospmatchpath: String)
case class alUploadItem(company: String)
case class alExportItem(datatype: String,market : List[String],staend : List[String],company : String,filetype : String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
	implicit val itemFormat = jsonFormat2(alCalcItem)
	implicit val itemFormatCommit = jsonFormat3(alCommitItem)
	implicit val itemFormatUpload = jsonFormat1(alUploadItem)
	implicit val itemFormatExport = jsonFormat5(alExportItem)
}

trait alAkkaHttpFunc extends Directives with JsonSupport{

	implicit def executionContext: ExecutionContext

	implicit def requestTimeout: Timeout

	val routes = alSampleCheckDataFunc ~ alCalcDataFunc ~ alTest ~ getRcommit ~ getCleanData ~ getFileEx

	def alTest = post {
		path("test") {
			complete("""{"result" : "Ok"}""")
		}
	}

	def alSampleCheckDataFunc = post {
		path("samplecheck") {
			complete("""{"result" : "Ok"}""")
		}
	}

	def alCalcDataFunc = post {
		path("calc") {
			entity(as[alCalcItem]) {item =>
				println(s"item = ${item.company}")
				println(s"item = ${item.filename}")
				complete("""{"resule" : "Ok"}""")
			}
		}
	}

	def getRcommit = post {
		path("commit") {
			entity(as[alCommitItem]) { item =>
				println(s"item=${item}")
				complete("""{"result":"Ok"}""")
			}
		}
	}

	def getCleanData = post {
		path("cleandata") {
			entity(as[alUploadItem]) { item =>
				println(s"company=${item.company}")
				val result = pyShell(item.company).excute
				val gson : Gson = new Gson()
				println(s"result=${gson.toJson(result)}")
				complete("""{"result":"""+gson.toJson(result)+"""}""")
			}
		}
	}

	def getFileEx = post {
		path("export") {
			entity(as[alExportItem]) { item =>
				val result = alFileExport(item.datatype,item.market,item.staend,item.company,item.filetype)
				val gson : Gson = new Gson()
				println(s"result=${gson.toJson(result)}")
				complete("""{"result":"""+gson.toJson(result)+"""}""")
			}
		}
	}
}
