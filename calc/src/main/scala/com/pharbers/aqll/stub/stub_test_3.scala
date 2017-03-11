package com.pharbers.aqll.stub

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{push_max_job, worker_register}
import com.pharbers.aqll.alcalc.almain.{alDriverSingleton, alMaxDriver}
import com.pharbers.aqll.calc.split.{EventCollector, SplitReceptionSingleton}
import com.typesafe.config.ConfigFactory

/**
  * Created by Alfred on 10/03/2017.
  */

// test cases : 分布式Masterces
object stub_test_3 extends App {
    val config = ConfigFactory.load("split-master")
    val system = ActorSystem("calc", config)
//    val node_ip = system.settings.config.getStringList("akka.cluster.seed-nodes")
//    val a = system.actorOf(SplitReceptionSingleton.props, SplitReceptionSingleton.name)
    if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
        Cluster(system).registerOnMemberUp {
            val a = system.actorOf(alDriverSingleton.props, "splitreception")
            println(s"a = $a")
            println("cluster ready")
            a ! push_max_job("""config/new_test/2016-01.xlsx""")
        }
    }
}