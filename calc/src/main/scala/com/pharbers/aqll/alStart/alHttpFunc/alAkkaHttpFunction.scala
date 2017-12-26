package com.pharbers.aqll.alStart.alHttpFunc

import akka.util.Timeout
import akka.actor.ActorSystem
import akka.http.scaladsl.server
import play.api.libs.json.Json._

import scala.collection.immutable.Map
import play.api.libs.json.Json.toJson

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import akka.http.scaladsl.server.Directives
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg._
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alCalcHelp.alAkkaHttpJson.PlayJsonSupport
import com.pharbers.aqll.alCalcHelp.alFinalDataProcess.alFileExport
import play.api.libs.json.OFormat

/**
  * Created by qianpeng on 2017/6/5.
  */
class alAkkaHttpFunctionApi(system: ActorSystem, timeout: Timeout) extends alAkkaHttpFunction {
	implicit val requestTimeout: Timeout = timeout
	implicit def executionContext: ExecutionContextExecutor = system.dispatcher
}

case class Item(str: String, lst: List[String])
case class alCalcYmItem(company: String, uid: String, cpa: String, gycx: String)
case class alPanelItem(company: String, uid: String, cpa: String, gycx: String, ym: List[String] = Nil)
case class alCalcItem(uid: String)
case class alCommitItem(uid: String)
case class alExportItem(uid: String, filetype: String, datatype: String, market: List[String], staend: List[String])

trait PlayJson extends PlayJsonSupport {
	implicit val itemJson: OFormat[Item] = format[Item]
	implicit val itemFormatCalcYm: OFormat[alCalcYmItem]  = format[alCalcYmItem]
	implicit val itemFormatPanel: OFormat[alPanelItem]  = format[alPanelItem]
	implicit val itemFormatCalc: OFormat[alCalcItem]  = format[alCalcItem]
	implicit val itemFormatCommitItem: OFormat[alCommitItem]  = format[alCommitItem]
	implicit val itemFormatExportItem: OFormat[alExportItem]  = format[alExportItem]
}

trait alAkkaHttpFunction extends Directives with PlayJson{
	implicit def executionContext: ExecutionContext
	implicit def requestTimeout: Timeout
	type route = server.Route

	val routes: route = alCalcYM ~ alCalcData ~ alGenternPanel ~ alDataCommit ~ alDataExport
	
	def Test: route = post {
		path("test") {
			entity(as[Item]) { item =>
				println(item)
				val result = toJson(Map("result" -> "ok"))
				complete(result)
			}
		}
	}

	def alCalcYM: route = post {
		path("calcYM") {
			entity(as[alCalcYmItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				a ! startCalcYm(alPanelItem(item.company, item.uid, item.cpa, item.gycx))
				complete(toJson(successToJson().get))
			}
		}
	}

	def alGenternPanel: route = post {
		path("genternPanel") {
			entity(as[alPanelItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				a ! startGeneratePanel(item)
				complete(toJson(successToJson().get))
			}
		}
	}

	def alCalcData: route = post {
		path("modelcalc") {
			entity(as[alCalcItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				a ! startCalc(item.uid)
				complete(toJson(successToJson().get))
			}
		}
	}

	def alDataCommit: route = post {
		path("datacommit") {
			entity(as[alCommitItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				println(item.uid)
				a ! startAggregationCalcData(item.uid)
				complete(Map("result" -> "ok"))
			}
		}
	}

	def alDataExport: route = post {
		path("dataExport") {
			entity(as[alExportItem]) { item =>
				val result = alFileExport(item).export
				complete(toJson(successToJson(toJson(Map("result" -> result)))))
			}
		}
	}
}
