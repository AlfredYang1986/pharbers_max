package com.pharbers.aqll.alStart.alHttpFunc

import akka.util.Timeout
import akka.actor.ActorSystem
import play.api.libs.json.Json._
import scala.collection.immutable.Map
import play.api.libs.json.Json.toJson
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives
import com.pharbers.aqll.alMSA.alCalcMaster.alCalcMsg._
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.alMSA.alClusterLister.alAgentIP.masterIP
import com.pharbers.aqll.alCalcHelp.alAkkaHttpJson.PlayJsonSupport

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
case class alCalcItem(uid: String)

trait PlayJson extends PlayJsonSupport {
	implicit val itemJson = format[Item]
	implicit val itemFormatCalcYm = format[alCalcYmItem]
	implicit val itemFormatPanel = format[alPanelItem]
	implicit val itemFormatCalc = format[alCalcItem]
}

trait alAkkaHttpFunction extends Directives with PlayJson{
	implicit def executionContext: ExecutionContext
	implicit def requestTimeout: Timeout

	val routes = alCalcYM ~ alCalcData ~ alGenternPanel

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
				a ! startCalcYm(alPanelItem(item.company, item.uid, item.cpa, item.gycx))
				complete(toJson(successToJson().get))
			}
		}
	}

	def alGenternPanel = post {
		path("genternPanel") {
			entity(as[alPanelItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				a ! startGeneratePanel(item)
				complete(toJson(successToJson().get))
			}
		}
	}

	def alCalcData = post {
		path("modelcalc") {
			entity(as[alCalcItem]) { item =>
				val a = alAkkaSystemGloble.system.actorSelection("akka.tcp://calc@"+ masterIP +":2551/user/agent-reception")
				a ! startCalc(item.uid)
				complete(toJson(successToJson().get))
			}
		}
	}
}
