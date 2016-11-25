package com.pharbers.aqll.calc.main

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import com.pharbers.aqll.calc.split.SplitReception.excelJobStart

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
	sample ! excelJobStart("""E:\文件\法伯相关\MAX改建\程序测试数据\客户上传\201601-07-CPA-HTN市场数据待上传.xlsx""")
}