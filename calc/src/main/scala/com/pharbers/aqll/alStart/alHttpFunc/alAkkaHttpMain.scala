package com.pharbers.aqll.alStart.alHttpFunc

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, server}
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContextExecutor, Future}

trait RequestTimeout {
	import scala.concurrent.duration._
	def requestTimeout(config: Config): Timeout = {
		val t = config.getString("akka.http.server.request-timeout")
		val d = Duration(t)
		FiniteDuration(d.length, d.unit)
	}
}

object alAkkaHttpSystem extends App with RequestTimeout {
//	var actorSystem : Option[ActorSystem] = None
	
	val config: Config = ConfigFactory.load("akka-http")
	val host: String = config.getString("akka.http.server.host")
	val port: Int = config.getInt("akka.http.server.port")
	
	implicit val system:ActorSystem = ActorSystem("calc", config)
	implicit val ec: ExecutionContextExecutor = system.dispatcher
	implicit val mat: ActorMaterializer = ActorMaterializer()
	
	val api: server.Route = new alAkkaHttpFunctionApi(system, requestTimeout(config)).routes
	
	val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port)
	bindingFuture.map ( _ => afterMain()).onFailure {case _ => system.terminate()}
	
	def afterMain(): Unit = {
		println(s"host = $host, port = $port")
		println(s"Akka Http 启动完成")
	}
}