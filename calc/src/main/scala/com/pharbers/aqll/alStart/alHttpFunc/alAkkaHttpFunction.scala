package com.pharbers.aqll.alStart.alHttpFunc

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, StandardRoute}
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alAkkaHttpJson.PlayJsonSupport
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary

import scala.concurrent.ExecutionContext
import play.api.libs.json.Json._
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.common.alFileHandler.clusterListenerConfig._
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.{alExport, alFileExport, alSampleCheck, alSampleCheckCommit}
import com.pharbers.aqll.common.alCmd.pycmd.pyCmd
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.{max_calc_done, push_filter_job}

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
case class alCommitItem(company: String, uuid: String, uname: String)
case class alExportItem(datatype: String, market: List[String],
                        staend: List[String], company: String,
                        filetype: String, uname: String)
case class alHttpCreateIMUser(name: String, pwd: String)

trait PlayJson extends PlayJsonSupport {
	implicit val itemJson = format[Item]
	
	implicit val itemFormatUpBefore = format[alUpBeforeItem]
	implicit val itemFormatUpload = format[alUploadItem]
	implicit val itemFormatCheck = format[alCheckItem]
	implicit val itemFormatCalc = format[alCalcItem]
	implicit val itemFormatCommit = format[alCommitItem]
	implicit val itemFormatExport = format[alExportItem]
	implicit val itemFormatUser = format[alHttpCreateIMUser]
}

trait alAkkaHttpFunction extends Directives with PlayJson{
	implicit def executionContext: ExecutionContext
	implicit def requestTimeout: Timeout
	
	val routes =  alSampleCheckDataFunc ~
		alNewCalcDataFunc ~ alNewModelOperationCommitFunc ~
		alFileUploadPythonFunc ~ alResultFileExportFunc ~
		alFileUploadPyBefore
	
	def Test = post {
		path("test") {
			entity(as[Item]) { item =>
				println(item.str)
				println(item.lst)
				val result = toJson(Map("result" -> "ok"))
				complete(result)
			}
		}
	}

	
	def alFileUploadPyBefore = post {
		path("uploadbefore") {
			entity(as[alUpBeforeItem]) { item =>
//				val result = pyCmd(s"$root$program$fileBase${item.company}" ,Upload_Firststep_Filename, "").excute
				val result = pyCmd(s"$fileBase${item.company}" ,Upload_Firststep_Filename, "").excute
//				toJson(Map("status" -> toJson("success"), "result" -> toJson(Map("result" -> "2016#", "page" -> ""))))
				alMessageProxy().sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				complete(result)
			}
		}
	}
	
	def alFileUploadPythonFunc = post {
		path("uploadfile") {
			entity(as[alUploadItem]) { item =>
//				val result = pyCmd(s"$root$program$fileBase${item.company}",Upload_Secondstep_Filename, item.yms).excute
				val result = pyCmd(s"$fileBase${item.company}",Upload_Secondstep_Filename, item.yms).excute
				complete(result)
			}
		}
	}
	
	def alSampleCheckDataFunc = post {
		path("samplecheck") {
			entity(as[alCheckItem]) {item =>
				val result = alSampleCheck().apply(item.company, item.filename, item.uname)
				alMessageProxy().sendMsg("100", item.uname, Map("uuid" -> "", "company" -> item.company, "type" -> "progress"))
				complete(result)
			}
		}
	}
	
	def alNewCalcDataFunc = post {
		path("modelcalc") {
			entity(as[alCalcItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")
				val path = fileBase + item.company + outPut + item.filename
				a ! push_filter_job(path, new alCalcParmary(item.company, item.uname))
				complete(toJson(successToJson().get))
			}
		}
	}
	
//	def alCalcDataFunc = post {
//		path("modelcalc") {
//			entity(as[alCalcItem]) {item =>
//				val a = alAkkaSystemGloble.system.actorSelection(singletonPaht)
//				val path = fileBase + item.company + outPut + item.filename
//				a ! filter_excel_jobs(path, new alCalcParmary(item.company, item.uname), a)
//				complete(toJson(successToJson().get))
//			}
//		}
//	}
	
	def alNewModelOperationCommitFunc = post {
		path("datacommit") {
			entity(as[alCommitItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")
				val map = Map("company" -> item.company, "uuid" -> item.uuid, "uname" -> item.uname)
				a ! max_calc_done(map)
				val result = alSampleCheckCommit().apply(item.company)
				complete(result)
			}
		}
	}
	
	//待测试
//	def alModelOperationCommitFunc = post {
//		path("datacommit") {
//			entity(as[alCommitItem]) { item =>
//				val a = alAkkaSystemGloble.system.actorSelection(singletonPaht)
//				a ! commit_finalresult_jobs(item.company, item.uuid)
//				val result = alSampleCheckCommit().apply(item.company)
//				complete(result)
//			}
//		}
//	}
	
	def alResultFileExportFunc = post {
		path("dataexport") {
			entity(as[alExportItem]) { item =>
				val alExportPram = alExport(item.datatype,
					item.market,
					item.staend,
					item.company,
					item.filetype,
					item.uname)
				val result = alFileExport().apply(alExportPram)
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
}
