package com.pharbers.aqll.calc.stub

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import com.pharbers.aqll.calc.maxmessages.excelJobStart
import com.pharbers.aqll.calc.split.JobCategories.cpaProductJob

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
        			      port = 2556
        			    }
        			  }
        			}
                """
    
    val config = ConfigFactory.parseString(conf)
    val forend = ActorSystem("forend",config)
    val path = "akka.tcp://calc@127.0.0.1:2552/user/splitreception"
	val sample = forend.actorSelection(path)
    
    println("start")
	sample ! excelJobStart("""config/test/BMS客户上传/201601-07-CPA-Baraclude产品待上传.xlsx""", cpaProductJob, "BMS", 0)
	println("end")
}