package com.pharbers.aqll.calc.stub

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.pharbers.aqll.calc.maxmessages.registerMaster
import com.pharbers.aqll.calc.split._


object StubWorkerMain extends App {
	val config = ConfigFactory.load("split-worker_1")
	val system = ActorSystem("calc", config)
	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
			println("register begin")
			val reception = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/splitreception")
			val master = system.actorOf(SplitMaster.props, "split-master")

			reception.tell(new registerMaster(), master)
		}
	}
}