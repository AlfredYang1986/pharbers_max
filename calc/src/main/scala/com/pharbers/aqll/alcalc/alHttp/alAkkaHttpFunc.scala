package com.pharbers.aqll.alcalc.alHttp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
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

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
	implicit val itemFormat = jsonFormat2(alCalcItem)
}

trait alAkkaHttpFunc extends Directives with JsonSupport{

	implicit def executionContext: ExecutionContext

	implicit def requestTimeout: Timeout

	val routes = alSampleCheckDataFunc ~ alCalcDataFunc ~ alTest

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
}
