package com.pharbers.aqll.calc.split

import akka.actor._
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import akka.cluster.singleton.ClusterSingletonProxy
import akka.cluster.singleton.ClusterSingletonProxySettings
import com.pharbers.aqll.calc.maxmessages.{excelJobStart, excelSplitStart, freeMaster, registerMaster}

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
				.withRole(None)
				.withSingletonName(SplitReception.name)
		), name = "singleton-master"
	)

	val reception = context.system.actorOf(
		ClusterSingletonProxy.props(
//			singletonManager.path.child(SplitReception.name)
//				.toStringWithoutAddress,
			"/user/singleton-master",
			ClusterSingletonProxySettings(context.system)
				.withRole(None)
				.withSingletonName("reception-actor")
		), "reception-actor"
	)

	def receive = {
		case cmd: excelSplitStart => reception forward cmd
		case cmd: excelJobStart => reception forward cmd
		case cmd: freeMaster => reception forward cmd
		case cmd: registerMaster => reception forward cmd
		case _ => println("cannot be here"); ???
	}
}
