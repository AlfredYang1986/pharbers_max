package com.pharbers.aqll.calc.split

import akka.actor._
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import akka.cluster.singleton.ClusterSingletonProxy
import akka.cluster.singleton.ClusterSingletonProxySettings
import com.pharbers.aqll.calc.maxmessages._

object SplitReceptionSingleton {
	def props = Props(new SplitReceptionSingleton)

	//    def name = "reception-singleton"
	def name = "splitreception"
}

class SplitReceptionSingleton extends Actor {
	val singletonManager = context.system.actorOf(
		ClusterSingletonManager.props(
			SplitReception.props,
			PoisonPill,
			ClusterSingletonManagerSettings(context.system)
				.withRole(Some("splitmaster"))
				.withSingletonName(SplitReception.name)
		), name = "singleton-master"
	)

	val reception = context.system.actorOf(
		ClusterSingletonProxy.props(
			singletonManager.path.toStringWithoutAddress,
			ClusterSingletonProxySettings(context.system)
				.withRole(Some("splitmaster"))
				.withSingletonName("reception-actor")
		), "reception-actor"
	)

	def receive = {
		case cmd: excelSplitStart => reception forward cmd
		case cmd: excelJobStart => reception forward cmd
		case cmd: freeMaster => reception forward cmd
		case cmd: registerMaster => reception forward cmd
        case cmd: requestMasterAverage => reception forward cmd
		case cmd: groupByResults => reception forward cmd
		case _ => println("cannot be here"); ???
	}
}