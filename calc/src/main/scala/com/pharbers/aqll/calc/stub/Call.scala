package com.pharbers.aqll.calc.stub

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem

object Call extends App{
    val  conf = """
                akka {
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
        			      port = 2552
        			    }
        			  }
        			}
                """
    
    val config = ConfigFactory.parseString(conf)
    val forend = ActorSystem("forend",config)
    val path = "akka.tcp://backend@127.0.0.1:2551/user/sample"
	val sample = forend.actorSelection(path)
    
    println("start")
	sample ! "cpaproduct"
	println("end")
}