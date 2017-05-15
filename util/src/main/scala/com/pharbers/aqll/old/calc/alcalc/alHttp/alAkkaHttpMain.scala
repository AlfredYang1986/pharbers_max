package com.pharbers.aqll.old.calc.alcalc.alHttp

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.pharbers.aqll.old.calc.alcalc.alAkkaListener.alAkkaListener
import com.pharbers.aqll.old.calc.alcalc.alSchedulerJobs.{alScheduleRemoveFiles, rmFile}
import com.pharbers.aqll.old.calc.alcalc.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register, worker_register}
import com.pharbers.aqll.old.calc.alcalc.almain.{alCalcActor, alDriverSingleton, alGroupActor}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future

/**
	* Created by qianpeng on 2017/3/26.
	*/

object alAkkaSystemGloble {
	var system : ActorSystem = null
}

object alAkkaHttpMain extends App with RequestTimeout {
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
		import scala.concurrent.duration._
		if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
			Cluster(system).registerOnMemberUp {
				println("cluster ready")
				alAkkaSystemGloble.system = system
				a ! group_register(w)
				a ! calc_register(c)
				a ! worker_register()
				val rm = system.actorOf(alScheduleRemoveFiles.props)
				system.scheduler.schedule(0 seconds, 10 seconds, rm, new rmFile())
			}
			system.actorOf(alAkkaListener.props, "akka-listener")
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
