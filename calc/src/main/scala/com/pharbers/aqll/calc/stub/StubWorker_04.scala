package com.pharbers.aqll.calc.stub

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.calc.split.{SplitMaster, register}
import com.pharbers.aqll.calc.util.GetProperties
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object StubWorker_04 extends App {
	val config = ConfigFactory.load("split-worker_4")
	val system = ActorSystem("calc", config)

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
			println("register begin")
			val reception = system.actorSelection(GetProperties.singletonPaht)
			val master = system.actorOf(SplitMaster.props, "split-master")
			system.scheduler.scheduleOnce(2 second, master, register())
		}
	}
}