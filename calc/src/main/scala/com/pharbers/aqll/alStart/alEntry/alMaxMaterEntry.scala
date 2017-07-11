package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alStart.alHttpFunc.alAkkaSystemGloble
import com.typesafe.config.ConfigFactory

/**
  * Created by alfredyang on 11/07/2017.
  */
object alMaxMaterEntry extends App {
    val config = ConfigFactory.load("split-master")
    val system = ActorSystem("calc", config)

    if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
        Cluster(system).registerOnMemberUp {
            println("start system success")
        }
    }
}
