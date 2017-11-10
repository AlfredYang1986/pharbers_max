package com.pharbers.aqll.alStart.alHttpFunc

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alAkkaHttpJson.PlayJsonSupport
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary

import scala.concurrent.ExecutionContext
import play.api.libs.json.Json._
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.alCalcOther.alMessgae.alMessageProxy
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.{alExport, alFileExport, alSampleCheck, alSampleCheckCommit}
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.{max_calc_done, push_filter_job}
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.{pushCalcYMJobs, pushGeneratePanelJobs}
import com.pharbers.http.HTTP

import scala.collection.immutable.Map

/**
  * Created by qianpeng on 2017/6/5.
  */
class alAkkaHttpFunctionApi(system: ActorSystem, timeout: Timeout) extends alAkkaHttpFunction {
	implicit val requestTimeout = timeout
	implicit def executionContext = system.dispatcher
}

case class Item(str: String, lst: List[String])

case class alUpBeforeItem(company: String, user: String, cpa: String, gycx: String)
case class alUploadItem(company: String, user: String, cpa: String, gycx: String, ym: List[String])
case class alCheckItem(company: String, filename: String, uname: String)
case class alCalcItem(filename: List[String], company: String, imuname: String, uid: String)
case class alCommitItem(company: String, uuid: String, uname: String, uid: String)
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

	val routes = Test ~ alSampleCheckDataFunc ~
		alNewCalcDataFunc ~ alNewModelOperationCommitFunc ~
		alGenternPanel ~ alResultFileExportFunc ~
		alCalcYM

	def Test = post {
		path("test") {
			entity(as[Item]) { item =>
				val company = "fefefefefefefefefe"
				val uuid = "fffffff"
				val msg = Map("file" -> "fuck.f", "uuid" -> uuid, "table" -> s"${company + uuid}", "type" -> "进度条", "step" -> "第一步", "data" -> "10")
				// 这里的str就是uid
				val json = toJson(Map("condition" -> Map("uid" -> toJson(item.str), "msg" -> toJson(msg)) ))
				HTTP("http://127.0.0.1:9000/akka/callback").header("Accept" -> "application/json", "Content-Type" -> "application/json").post(json)
				val result = toJson(Map("result" -> "ok"))
				complete(result)
			}
		}
	}

	def alCalcYM = post {
		path("calcYM") {
			entity(as[alUpBeforeItem]) { item =>
				val msg = Map("ym" -> "201705", "type" -> "进度条", "step" -> "第一步", "data" -> "10")
				val json = toJson(Map("condition" -> Map("uid" -> toJson(item.user), "msg" -> toJson(msg)) ))
				HTTP("http://127.0.0.1:9000/akka/callback").header("Accept" -> "application/json", "Content-Type" -> "application/json").post(json)

//				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")
//				a ! pushCalcYMJobs(item)
				complete(toJson(successToJson().get))
			}
		}
	}

	def alGenternPanel = post {
		path("genternPanel") {
			entity(as[alUploadItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")
				a ! pushGeneratePanelJobs(item)
				complete(toJson(successToJson().get))
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
				item.filename foreach { x =>
					val path = fileBase + item.company + outPut + x
					a ! push_filter_job(path, new alCalcParmary(item.company, item.imuname, item.uid))
				}
				complete(toJson(successToJson().get))
			}
		}
	}
	
	def alNewModelOperationCommitFunc = post {
		path("datacommit") {
			entity(as[alCommitItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")
				val map = Map("company" -> item.company, "uuid" -> item.uuid, "uname" -> item.uname, "uid" -> item.uid)
				a ! max_calc_done(map)
				val result = alSampleCheckCommit().apply(item.company)
				complete(result)
			}
		}
	}
	
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
//				alIMUser.createUser(item.name, item.pwd)
				complete(toJson(successToJson().get))
			}
		}
	}
}
