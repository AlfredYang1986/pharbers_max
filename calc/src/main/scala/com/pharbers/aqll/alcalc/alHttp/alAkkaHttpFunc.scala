package com.pharbers.aqll.alcalc.alHttp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import com.google.gson.Gson
import com.pharbers.aqll.alcalc.alcmd.pyshell.pyShell
import com.pharbers.aqll.alcalc.alfinaldataprocess.alFileExport.alFileExport
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.filter_excel_jobs
import com.pharbers.aqll.alcalc.almaxdefines.alCalcParmary
import com.pharbers.aqll.util.GetProperties
import spray.json.DefaultJsonProtocol
import com.pharbers.aqll.alcalc.alfinaldataprocess.alSampleCheck._
import scala.concurrent.ExecutionContext

/**
  * Created by qianpeng on 2017/3/26.
  */

class alAkkaHttpFuncApi(system: ActorSystem, timeout: Timeout) extends alAkkaHttpFunc {
	implicit val requestTimeout = timeout

	implicit def executionContext = system.dispatcher
}

case class alUploadItem(company: String)
case class alCheckItem(company: String)
case class alCalcItem(filename: String, company: String)
case class alCommitItem(company: String)
case class alExportItem(datatype: String,market : List[String],staend : List[String],company : String,filetype : String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
	implicit val itemFormatUpload = jsonFormat1(alUploadItem)
	implicit val itemFormatCheck = jsonFormat1(alCheckItem)
	implicit val itemFormatCalc = jsonFormat2(alCalcItem)
	implicit val itemFormatCommit = jsonFormat1(alCommitItem)
	implicit val itemFormatExport = jsonFormat5(alExportItem)
}

trait alAkkaHttpFunc extends Directives with JsonSupport{

	implicit def executionContext: ExecutionContext

	implicit def requestTimeout: Timeout

	val routes = alTest ~ alSampleCheckDataFunc ~ alCalcDataFunc ~ alModelOperationCommitFunc ~ alFileUploadPythonFunc ~ alResultFileExportFunc

	def alTest = post {
		path("test") {
			complete("""{"result" : "Ok"}""")
		}
	}

	def alFileUploadPythonFunc = post {
		path("uploadfile") {
			entity(as[alUploadItem]) { item =>
				println(s"company=${item.company}")
				val result = pyShell(item.company).excute
				val gson: Gson = new Gson()
				println(s"result=${gson.toJson(result)}")
				complete("""{"result":""" + gson.toJson(result) +"""}""")
			}
		}
	}

		def alSampleCheckDataFunc = post {
		path("samplecheck") {
			entity(as[alCheckItem]) {item =>
				println(s"item = ${item.company}")
				alSampleCheck(item.company)
				complete("""{"result" : "Ok"}""")
			}
		}
	}

	def alCalcDataFunc = post {
		path("calc") {
			entity(as[alCalcItem]) {item =>
				println(s"item = ${item.company}")
				println(s"item = ${item.filename}")
				println(s"path = ${val path = GetProperties.fileBase + item.company + "/" + GetProperties.outPut + item.filename}")
//				val a = alAkkaSystemGloble.system.actorSelection(GetProperties.singletonPaht)
//				val path = GetProperties.fileBase + item.company + "/" + GetProperties.outPut + item.filename
//				filter_excel_jobs(path, new alCalcParmary(item.company), a)
				complete("""{"resule" : "Ok"}""")
			}
		}
	}

	def alModelOperationCommitFunc = post {
		path("datacommit") {
			entity(as[alCommitItem]) { item =>
				println(s"item=${item.company}")
				complete("""{"result":"Ok"}""")
			}
		}
	}

	def alResultFileExportFunc = post {
		path("dataexport") {
			entity(as[alExportItem]) { item =>
				val result = alFileExport(item.datatype,item.market,item.staend,item.company,item.filetype)
				val gson : Gson = new Gson()
				println(s"result=${gson.toJson(result)}")
				complete("""{"result":"""+gson.toJson(result)+"""}""")
			}
		}
	}
}
