package com.pharbers.aqll.calc.stub

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem

object StubWorkerMain extends App{
    val config = ConfigFactory.load("split-worker")
	val system = ActorSystem("calc", config) 
}