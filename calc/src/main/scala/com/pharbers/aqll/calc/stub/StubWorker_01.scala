package com.pharbers.aqll.calc.stub

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem}
import akka.cluster.Cluster
import com.pharbers.aqll.calc.split._
import com.pharbers.aqll.calc.util.GetProperties
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


object StubWorker_01 extends App {
	val config = ConfigFactory.load("split-worker_1")
	val system = ActorSystem("calc", config)

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
			println("register begin")
			val reception = system.actorSelection(GetProperties.singletonPaht)
			val master = system.actorOf(SplitMaster.props, "split-master")
			system.scheduler.scheduleOnce(2 seconds, master, register())
		}
	}
}