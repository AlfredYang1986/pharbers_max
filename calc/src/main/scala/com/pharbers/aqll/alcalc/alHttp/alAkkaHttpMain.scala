package com.pharbers.aqll.alcalc.alHttp

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register}
import com.pharbers.aqll.alcalc.almain.{alCalcActor, alDriverSingleton, alGroupActor}
//import com.pharbers.aqll.calc.split.EventCollector
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future

/**
  * Created by qianpeng on 2017/3/26.
  */

object CheckGloble {
	var system : akka.actor.ActorSystem = null
}

object alAkkaHttpMain extends App with RequestTimeout{
	val config = ConfigFactory.load("application")
	val host = config.getString("http.host")
	val port = config.getInt("http.port")

	implicit val system = ActorSystem("HttpMain")
	implicit val ec = system.dispatcher

	val api = new alAkkaHttpFuncApi(system, requestTimeout(config)).routes

	implicit val materializer = ActorMaterializer()
	val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port)

	bindingFuture.map { serverBinding =>
		stubmain
		println(s"serverBinding = ${serverBinding}")
	}.onFailure {
		case ex: Exception =>
			system.terminate()
	}

	def stubmain = {
		val config = ConfigFactory.load("split-master")
		val system = ActorSystem("calc", config)
		val w = system.actorOf(alGroupActor.props)
		val c = system.actorOf(alCalcActor.props)
		val a = system.actorOf(alDriverSingleton.props, "splitreception")
		if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
			Cluster(system).registerOnMemberUp {
				println("cluster ready")
				CheckGloble.system = system
				a ! group_register(w)
				a ! calc_register(c)
			}
//			system.actorOf(Props(new EventCollector), "cluster-listener")
		}
	}
}

trait RequestTimeout {
	import scala.concurrent.duration._
	def requestTimeout(config: Config): Timeout = {
		val t = config.getString("akka.http.server.request-timeout")
		val d = Duration(t)
		FiniteDuration(d.length, d.unit)
	}
}
