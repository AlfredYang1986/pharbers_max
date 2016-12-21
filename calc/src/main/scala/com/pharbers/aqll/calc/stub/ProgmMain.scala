package com.pharbers.aqll.calc.stub

import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props

class ProgmMain extends App{
    val conf = """akka {
					  loglevel = DEBUG
					  stdout-loglevel = WARNING
					  loggers = ["akka.event.slf4j.Slf4jLogger"]
					
					  actor {
					    provider = "akka.remote.RemoteActorRefProvider"
					  }
					
					  remote {
					    enabled-transports = ["akka.remote.netty.tcp"]
					    netty.tcp {
					      hostname = "127.0.0.1"
					      port = 2551
					    }
					  }
					}
              """
    
    val config = ConfigFactory.parseString(conf)
	val backend = ActorSystem("backend", config)
	println(config)
	backend.actorOf(Props[Sample], "sample")
}