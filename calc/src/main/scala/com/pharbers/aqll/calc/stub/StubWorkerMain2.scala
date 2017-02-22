package com.pharbers.aqll.calc.stub

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.calc.maxmessages.registerMaster
import com.pharbers.aqll.calc.split.{SplitMaster, SplitReception}
import com.typesafe.config.ConfigFactory


object StubWorkerMain2 extends App{
    val config = ConfigFactory.load("split-worker_2")
	val system = ActorSystem("calc", config)
//    system.actorOf(SplitReception.props, "splitreception")

	if(system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
			println("register begin")
			val reception = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/splitreception")
			val master = system.actorOf(SplitMaster.props, "split-master")

			reception.tell(new registerMaster(), master)
		}
	}


}