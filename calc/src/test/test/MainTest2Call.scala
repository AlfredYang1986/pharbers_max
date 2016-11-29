package test

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import com.pharbers.aqll.calc.maxmessages.excelJobStart

object MainTest2Call extends App{
    val conf = """akka {
					  loglevel = DEBUG
					  stdout-loglevel = WARNING
					  loggers = ["akka.event.slf4j.Slf4jLogger"]
					
					  actor {
					    provider = "akka.cluster.ClusterActorRefProvider"
					  }
					
					  remote {
					    enabled-transports = ["akka.remote.netty.tcp"]
					    netty.tcp {
					      hostname = "0.0.0.0"
					      port = 4711
					    }
					  }
					}
				""" 
    
    val config = ConfigFactory.parseString(conf)
	val forend = ActorSystem("forend", config)
	
	val path = "akka.tcp://calc@127.0.0.1:2551/user/splitreception"
	val sample = forend.actorSelection(path)
	println(sample)
	import com.pharbers.aqll.calc.split.JobCategories._
	sample ! excelJobStart("""config/test/201601-07-CPA-HTN市场数据待上传.xlsx""", cpaMarketJob)
	
//	sample ! excelJobStart("""config/test/2016 01-07-CPA-Taxol产品待上传.xlsx""", cpaProductJob)
	
}