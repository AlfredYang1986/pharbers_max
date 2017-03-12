package com.pharbers.aqll.stub

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register, push_max_job, worker_register}
import com.pharbers.aqll.alcalc.almain.{alCalcActor, alGroupActor, alDriverSingleton, alMaxDriver}
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
            println("cluster ready")
            val w = system.actorOf(alGroupActor.props)
            val c = system.actorOf(alCalcActor.props)
            val a = system.actorOf(alDriverSingleton.props, "splitreception")
            println(a)
            println(a.path)

            a ! group_register(w)
            a ! calc_register(c)
            a ! push_max_job("""config/new_test/2016-01.xlsx""")
        }
    }
}