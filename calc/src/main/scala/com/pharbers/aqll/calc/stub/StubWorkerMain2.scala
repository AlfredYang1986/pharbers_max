package com.pharbers.aqll.calc.stub

import akka.actor.ActorSystem
import com.pharbers.aqll.calc.split.SplitReception
import com.typesafe.config.ConfigFactory


object StubWorkerMain2 extends App{
    val config = ConfigFactory.load("split-worker_2")
	val system = ActorSystem("calc", config)
    system.actorOf(SplitReception.props, "splitreception")
}