package com.pharbers.aqll.alStart.alHttpFunc

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directives, StandardRoute}
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alAkkaHttpJson.PlayJsonSupport
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary

import scala.concurrent.ExecutionContext
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.common.alFileHandler.clusterListenerConfig._
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.{alExport, alFileExport, alSampleCheck, alSampleCheckCommit}
import com.pharbers.aqll.common.alCmd.pycmd.pyCmd
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

/**
  * Created by qianpeng on 2017/6/5.
  */
class alAkkaHttpFunctionApi(system: ActorSystem, timeout: Timeout) extends alAkkaHttpFunction {
	implicit val requestTimeout = timeout
	implicit def executionContext = system.dispatcher
}

case class Item(str: String, lst: List[String])

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

trait PlayJson extends PlayJsonSupport {
	implicit val itemJson = Json.format[Item]
	
	implicit val itemFormatUpBefore = Json.format[alUpBeforeItem]
	implicit val itemFormatUpload = Json.format[alUploadItem]
	implicit val itemFormatCheck = Json.format[alCheckItem]
	implicit val itemFormatCalc = Json.format[alCalcItem]
	implicit val itemFormatCommit = Json.format[alCommitItem]
	implicit val itemFormatExport = Json.format[alExportItem]
	implicit val itemFormatUser = Json.format[alHttpCreateIMUser]
	implicit val itemFormatQueryUUID = Json.format[alQueryUUIDItem]
}

trait alAkkaHttpFunction extends Directives with PlayJson{
	implicit def executionContext: ExecutionContext
	implicit def requestTimeout: Timeout
	
	val routes = Test ~ alSampleCheckDataFunc ~
				 alCalcDataFunc ~ alModelOperationCommitFunc ~
				 alFileUploadPythonFunc ~ alResultFileExportFunc ~
				 alFileUploadPyBefore ~ alQueryUUIDFunc
	
	def Test = post {
		path("test") {
			entity(as[Item]) { item =>
				println(item.str)
				println(item.lst)
				complete("""{"result": "OK"}""")
			}
		}
	}
	
	def alFileUploadPyBefore = post {
		path("uploadbefore") {
			entity(as[alUpBeforeItem]) { item =>
				new alMessageProxy().sendMsg("10", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				val result = pyCmd(s"$root$program$fileBase${item.company}" ,Upload_Firststep_Filename, "").excute
				new alMessageProxy().sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				complete(result)
			}
		}
	}
	
	def alFileUploadPythonFunc = post {
		path("uploadfile") {
			entity(as[alUploadItem]) { item =>
				new alMessageProxy().sendMsg("10", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				val result = pyCmd(s"$root$program$fileBase${item.company}",Upload_Secondstep_Filename, item.yms).excute
				new alMessageProxy().sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				complete(result)
			}
		}
	}
	
	def alSampleCheckDataFunc = post {
		path("samplecheck") {
			entity(as[alCheckItem]) {item =>
				val result = alSampleCheck().apply(item.company, item.filename, item.uname)
				new alMessageProxy().sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				complete(result)
			}
		}
	}
	
	def alCalcDataFunc = post {
		path("modelcalc") {
			entity(as[alCalcItem]) {item =>
				val a = alAkkaSystemGloble.system.actorSelection(singletonPaht)
				val path = fileBase + item.company + outPut + item.filename
				a ! filter_excel_jobs(path, new alCalcParmary(item.company, item.uname), a)
				complete(toJson(successToJson().get))
			}
		}
	}
	def alModelOperationCommitFunc = post {
		path("datacommit") {
			entity(as[alCommitItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection(singletonPaht)
				a ! commit_finalresult_jobs(item.company)
				val result = alSampleCheckCommit().apply(item.company)
				complete(result)
			}
		}
	}
	
	def alResultFileExportFunc = post {
		path("dataexport") {
			entity(as[alExportItem]) { item =>
				val alExport = new alExport(item.datatype,
					item.market,
					item.staend,
					item.company,
					item.filetype,
					item.uname)
				val result = alFileExport().apply(alExport)
				new alMessageProxy().sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				complete(result)
			}
		}
	}
	
	def alCreateIMUserFunc = post {
		path("createimuser") {
			entity(as[alHttpCreateIMUser]) { item =>
				//alIMUser.createUser(item.name, item.pwd)
				complete(toJson(successToJson().get))
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
				val result = toJson(successToJson(toJson(uuid)).get)
				complete(result)
			}
		}
	}
	
}
