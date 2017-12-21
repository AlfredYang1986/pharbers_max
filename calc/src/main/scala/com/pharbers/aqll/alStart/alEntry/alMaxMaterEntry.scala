package com.pharbers.aqll.alStart.alEntry

import akka.cluster.Cluster
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.pharbers.aqll.alMSA.alCalcAgent.alAgentSingleton
import com.pharbers.aqll.alMSA.alClusterLister.alMaxClusterLister

/**
  * Created by alfredyang on 11/07/2017.
  */
object alMaxMaterEntry extends App {
    val config = ConfigFactory.load("split-new-master")
    val system = ActorSystem("calc", config)
    if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
        Cluster(system).registerOnMemberUp {
            system.actorOf(alAgentSingleton.props, alAgentSingleton.name)
            system.actorOf(Props[alMaxClusterLister], "akka-listener")
        }
    }
}