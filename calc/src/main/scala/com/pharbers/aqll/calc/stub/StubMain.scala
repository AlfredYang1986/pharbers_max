package com.pharbers.aqll.calc.stub

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.cluster.Cluster
import akka.actor.Props

import com.pharbers.aqll.calc.split.SplitReception
import com.pharbers.aqll.calc.split.SplitReception.excelJobStart

object StubMain extends App {
	val config = ConfigFactory.load("split-master")
	val system = ActorSystem("calc", config) 
	
	if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
    	Cluster(system).registerOnMemberUp {
			system.actorOf(Props[SplitReception], "splitreception")
    	}
  	}  
}