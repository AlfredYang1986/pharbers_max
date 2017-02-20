package com.pharbers.aqll.calc.stub

import akka.actor.ActorSystem
import com.pharbers.aqll.calc.maxmessages.registerMaster
import com.pharbers.aqll.calc.split.{SplitMaster, SplitReception}
import com.typesafe.config.ConfigFactory


object StubWorkerMain2 extends App{
    val config = ConfigFactory.load("split-worker_2")
	val system = ActorSystem("calc", config)
//    system.actorOf(SplitReception.props, "splitreception")

    val reception = system.actorSelection("akka.tcp://backend@127.0.0.1:2551/user/splitreception")
    val master = system.actorOf(SplitMaster.props, "split-master")

    reception.tell(new registerMaster(), master)
}