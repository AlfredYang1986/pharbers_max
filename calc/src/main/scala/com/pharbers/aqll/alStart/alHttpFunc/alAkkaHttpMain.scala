package com.pharbers.aqll.alStart.alHttpFunc

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alCalc.almain.{alCalcActor, alGroupActor}
import com.pharbers.aqll.alCalcEnergy.alAkkaMonitoring.alAkkaMonitor
import com.pharbers.aqll.alCalcEnergy.alDriverSingleton
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.alCalcOther.alRemoveJobs.{alScheduleRemoveFiles, rmFile}
import com.pharbers.aqll.common.alFileHandler.clusterListenerConfig.singletonPaht
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
	val host = config.getString("akka.http.server.host")
	val port = config.getInt("akka.http.server.port")

	implicit val system = ActorSystem("HttpMain")
	implicit val ec = system.dispatcher

	val api = new alAkkaHttpFuncApi(system, requestTimeout(config)).routes

	implicit val materializer = ActorMaterializer()
	val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port)

	bindingFuture.map { serverBinding =>
		stubmain
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
				alAkkaSystemGloble.system = system
				a ! group_register(w)
				a ! calc_register(c)
				a ! worker_register()
				val rm = system.actorOf(alScheduleRemoveFiles.props)
				system.scheduler.schedule(0 seconds, 10 seconds, rm, new rmFile())
			}
			system.actorOf(alAkkaMonitor.props, "akka-listener")
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
