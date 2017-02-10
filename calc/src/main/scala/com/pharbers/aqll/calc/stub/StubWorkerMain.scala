package com.pharbers.aqll.calc.stub

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props}
import com.pharbers.aqll.calc.split.{ClusterEventListener, EventCollector, SplitReception}


object StubWorkerMain extends App{
    val config = ConfigFactory.load("split-worker_1")
	val system = ActorSystem("calc", config)
    system.actorOf(SplitReception.props, "splitreception")
}