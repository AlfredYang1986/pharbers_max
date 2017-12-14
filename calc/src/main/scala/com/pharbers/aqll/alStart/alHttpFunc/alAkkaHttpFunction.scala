package com.pharbers.aqll.alStart.alHttpFunc

import akka.util.Timeout
import akka.actor.ActorSystem
import play.api.libs.json.Json._
import scala.collection.immutable.Map
import play.api.libs.json.Json.toJson
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.alCalaHelp.alAkkaHttpJson.PlayJsonSupport
import com.pharbers.aqll.alCalcOther.alfinaldataprocess.{alExport, alFileExport, alSampleCheck, alSampleCheckCommit}
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{masterIP, pushCalcYMJob, pushGeneratePanelJob, pushSplitPanel}

/**
  * Created by qianpeng on 2017/6/5.
  */
class alAkkaHttpFunctionApi(system: ActorSystem, timeout: Timeout) extends alAkkaHttpFunction {
	implicit val requestTimeout = timeout
	implicit def executionContext = system.dispatcher
}

case class Item(str: String, lst: List[String])
case class alCalcYmItem(company: String, uid: String, cpa: String, gycx: String)
case class alPanelItem(company: String, uid: String, cpa: String, gycx: String, ym: List[String] = Nil)
case class alCheckItem(company: String, filename: String, uname: String)
case class alCalcItem(uid: String)
case class alCalcItem2(filename: List[String], company: String, imuname: String, uid: String)
case class alCommitItem(company: String, uuid: String, uname: String, uid: String)
case class alExportItem(datatype: String, market: List[String],
                        staend: List[String], company: String,
                        filetype: String, uname: String)
case class alHttpCreateIMUser(name: String, pwd: String)

trait PlayJson extends PlayJsonSupport {
	implicit val itemJson = format[Item]

	implicit val itemFormatCalcYm = format[alCalcYmItem]
	implicit val itemFormatPanel = format[alPanelItem]
	implicit val itemFormatCheck = format[alCheckItem]
	implicit val itemFormatCalc = format[alCalcItem]
	implicit val itemFormatCalc2 = format[alCalcItem2]
	implicit val itemFormatCommit = format[alCommitItem]
	implicit val itemFormatExport = format[alExportItem]
	implicit val itemFormatUser = format[alHttpCreateIMUser]
}

trait alAkkaHttpFunction extends Directives with PlayJson{
	implicit def executionContext: ExecutionContext
	implicit def requestTimeout: Timeout

	val routes = alSampleCheckDataFunc ~
		alNewCalcDataFunc ~ alNewModelOperationCommitFunc ~
		alGenternPanel ~ alResultFileExportFunc ~
		alCalcYM

	def Test = post {
		path("test") {
			entity(as[Item]) { item =>
				val result = toJson(Map("result" -> "ok"))
				complete(result)
			}
		}
	}

	def alCalcYM = post {
		path("calcYM") {
			entity(as[alCalcYmItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				a ! pushCalcYMJob(alPanelItem(item.company, item.uid, item.cpa, item.gycx))
				complete(toJson(successToJson().get))
			}
		}
	}

	def alGenternPanel = post {
		path("genternPanel") {
			entity(as[alPanelItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				a ! pushGeneratePanelJob(item)
				complete(toJson(successToJson().get))
			}
		}
	}

	def alSampleCheckDataFunc = post {
		path("samplecheck") {
			entity(as[alCheckItem]) {item =>
				val result = alSampleCheck().apply(item.company, item.filename, item.uname)
				complete(result)
			}
		}
	}

	def alNewCalcDataFunc = post {
		path("modelcalc") {
			entity(as[alCalcItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				a ! pushSplitPanel(item.uid)
				complete(toJson(successToJson().get))
			}
		}
	}
	
	def alNewModelOperationCommitFunc = post {
		path("datacommit") {
			entity(as[alCommitItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				val map = Map("company" -> item.company, "uuid" -> item.uuid, "uname" -> item.uname, "uid" -> item.uid)
//				a ! max_calc_done(map)
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
				complete(result)
			}
		}
	}

}
