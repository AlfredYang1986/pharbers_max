package com.pharbers.aqll.alcalc.alHttp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import com.google.gson.Gson
import com.pharbers.aqll.alcalc.alemchat.sendMessage
import com.pharbers.aqll.alcalc.alemchat.alIMUser
import com.pharbers.aqll.alcalc.alfinaldataprocess.alFileExport.alFileExport
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{check_excel_jobs, commit_finalresult_jobs, filter_excel_jobs}
import com.pharbers.aqll.alcalc.almaxdefines.alCalcParmary
import spray.json.DefaultJsonProtocol
import com.pharbers.aqll.alcalc.alfinaldataprocess.{alFilesExport, alSampleCheck}
import com.pharbers.aqll.common.alCmd.pycmd.pyCmd

import scala.concurrent.ExecutionContext
import com.pharbers.aqll.alcalc.alCommon.fileConfig._
import com.pharbers.aqll.alcalc.alCommon.clusterListenerConfig._

/**
  * Created by qianpeng on 2017/3/26.
  */

class alAkkaHttpFuncApi(system: ActorSystem, timeout: Timeout) extends alAkkaHttpFunc {
	implicit val requestTimeout = timeout

	implicit def executionContext = system.dispatcher
}

case class alUpBeforeItem(company: String, uname: String)
case class alUploadItem(company: String, yms: String, uname: String)
case class alCheckItem(company: String, filename: String, uname: String)
case class alCalcItem(filename: String, company: String, uname: String)
case class alCommitItem(company: String)
case class alExportItem(datatype: String, market: List[String],
                        staend: List[String], company: String,
                        filetype: String, uname: String)
case class alHttpCreateIMUser(name: String, pwd: String)
case class alQueryUUIDItem(company: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
	implicit val itemFormatUpBefore = jsonFormat2(alUpBeforeItem)
	implicit val itemFormatUpload = jsonFormat3(alUploadItem)
	implicit val itemFormatCheck = jsonFormat3(alCheckItem)
	implicit val itemFormatCalc = jsonFormat3(alCalcItem)
	implicit val itemFormatCommit = jsonFormat1(alCommitItem)
	implicit val itemFormatExport = jsonFormat6(alExportItem)
	implicit val itemFormatUser = jsonFormat2(alHttpCreateIMUser)
	implicit val itemFormatQueryUUID = jsonFormat1(alQueryUUIDItem)
}

trait alAkkaHttpFunc extends Directives with JsonSupport{

	implicit def executionContext: ExecutionContext

	implicit def requestTimeout: Timeout

	val routes = alTest ~ alSampleCheckDataFunc ~
		         alCalcDataFunc ~ alModelOperationCommitFunc ~
		         alFileUploadPythonFunc ~ alResultFileExportFunc ~
		         alFileUploadPyBefore ~ alQueryUUIDFunc

	def alTest = post {
		path("test") {
			complete("""{"result" : "Ok"}""")
		}
	}

	def alFileUploadPyBefore = post {
		path("uploadbefore") {
			entity(as[alUpBeforeItem]) { item =>
				sendMessage.sendMsg("10", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				val result = pyCmd(s"$root$program$fileBase${item.company}$python", "MaximumLikelyMonth.py", item.company, "").excute
				sendMessage.sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				complete("""{"result":""" + result +"""}""")
			}
		}
	}

	def alFileUploadPythonFunc = post {
		path("uploadfile") {
			entity(as[alUploadItem]) { item =>
				sendMessage.sendMsg("10", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				val result = pyCmd(s"$root$program$fileBase${item.company}$python", "GeneratePanelFile.py", item.company, item.yms).excute
				sendMessage.sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				complete("""{"result":""" + result +"""}""")
			}
		}
	}

	def alSampleCheckDataFunc = post {
		path("samplecheck") {
			entity(as[alCheckItem]) {item =>
				//println(s"company = ${item.company} filename = ${item.filename}")
				//val a = alAkkaSystemGloble.system.actorSelection(singletonPaht)
				//a ! check_excel_jobs(item.company,item.filename)
				println(s"company=${item.company} filename=${item.filename}")
				alSampleCheck(item.company, item.filename, item.uname)
				sendMessage.sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				complete("""{"result" : "Ok"}""")
			}
		}
	}

	def alCalcDataFunc = post {
		path("modelcalc") {
			entity(as[alCalcItem]) {item =>
				println(s"item = ${item.company}")
				println(s"item = ${item.filename}")
				println(s"path = ${fileBase + item.company + outPut + item.filename}")
				val a = alAkkaSystemGloble.system.actorSelection(singletonPaht)
				val path = fileBase + item.company + outPut + item.filename
				a ! filter_excel_jobs(path, new alCalcParmary(item.company, item.uname), a)
				complete("""{"resule" : "Ok"}""")
			}
		}
	}
	import com.pharbers.aqll.alcalc.alfinaldataprocess.alSampleCheckCommit
	def alModelOperationCommitFunc = post {
		path("datacommit") {
			entity(as[alCommitItem]) { item =>
				println(s"item=${item.company}")
				val a = alAkkaSystemGloble.system.actorSelection(singletonPaht)
				a ! commit_finalresult_jobs(item.company)
				alSampleCheckCommit(item.company)
				complete("""{"result":"Ok"}""")
			}
		}
	}

	def alResultFileExportFunc = post {
		path("dataexport") {
			entity(as[alExportItem]) { item =>
				val alExport = new alFilesExport(item.datatype,
												item.market,
												item.staend,
												item.company,
												item.filetype,
												item.uname)
				val result = alFileExport(alExport)
				sendMessage.sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				val gson : Gson = new Gson()
				println(s"result=${gson.toJson(result)}")
				complete("""{"result":"""+gson.toJson(result)+"""}""")
			}
		}
	}

	def alCreateIMUserFunc = post {
		path("createimuser") {
			entity(as[alHttpCreateIMUser]) { item =>
				alIMUser.createUser(item.name, item.pwd)
				complete("""{"result": "OK"}""")
			}
		}
	}

	def alQueryUUIDFunc = post {
		path("queryUUID") {
			entity(as[alQueryUUIDItem]) { item =>
				val uuid = alCalcParmary.alParmary.single.get.find(_.company.equals(item.company)) match {
					case None => "fb9cb2cd-52ab-4493-b943-24800d85a610"
					case Some(x) => x.uuid.toString
				}
				val gson : Gson = new Gson()
				complete("""{"result": """+gson.toJson(uuid)+"""}""")
			}
		}
	}
}
