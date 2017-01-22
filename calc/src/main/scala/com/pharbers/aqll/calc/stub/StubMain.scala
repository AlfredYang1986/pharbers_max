package com.pharbers.aqll.calc.stub

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import akka.cluster.Cluster
import com.pharbers.aqll.calc.split.{ClusterEventListener, EventCollector, SplitReception}
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.excel.Manage.AdminHospitalDataBase
import com.pharbers.aqll.calc.maxmessages.excelJobStart
import com.pharbers.aqll.calc.util.ListQueue

object StubMain extends App {
	val config = ConfigFactory.load("split-master")
	val system = ActorSystem("calc", config)
//    val node_ip = system.settings.config.getStringList("akka.cluster.seed-nodes")
//    println(config.getStringList("akka.cluster.seed-nodes"))
//	if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
//        for(i <- 1 to (node_ip.toArray.size-1)) {
//            val c = ConfigFactory.load("split-worker_"+i)
//            val s = ActorSystem("calc", c)
//            val a = s.actorOf(AkkaReception.props)
//            ListQueue.ListNode_Queue((0, a, node_ip.get(i-1)))
//        }
//        println(ListQueue.listnode)
    	Cluster(system).registerOnMemberUp {
			system.actorOf(AkkaReception.props, "splitreception")
    	}
        system.actorOf(Props(new EventCollector), "cluster-listener")
}