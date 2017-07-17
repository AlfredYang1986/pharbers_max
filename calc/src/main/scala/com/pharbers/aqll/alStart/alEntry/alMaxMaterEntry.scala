package com.pharbers.aqll.alStart.alEntry

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.pharbers.aqll.alMSA.alCalcAgent.alAgentSingleton
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster
import com.pharbers.aqll.alMSA.alClusterLister.alMaxClusterLister
import com.typesafe.config.ConfigFactory

/**
  * Created by alfredyang on 11/07/2017.
  */
object alMaxMaterEntry extends App {
    val config = ConfigFactory.load("split-new-master")
    val system = ActorSystem("calc", config)

    if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
        Cluster(system).registerOnMemberUp {
            println("start system success")
            system.actorOf(alMaxMaster.props, alMaxMaster.name)
            system.actorOf(alAgentSingleton.props, alAgentSingleton.name)
            system.actorOf(Props[alMaxClusterLister], "akka-listener")
        }
    }
}
