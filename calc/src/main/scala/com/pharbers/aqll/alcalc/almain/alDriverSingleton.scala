package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{finish_split_excel_job, push_max_job, worker_register}
import com.pharbers.aqll.calc.split.SplitReception

/**
  * Created by qianpeng on 2017/3/11.
  */

object alDriverSingleton {
	def props = Props(new alDriverSingleton)
	def name = "splitreception"
}

class alDriverSingleton extends Actor with ActorLogging{
	val singletonManager = context.system.actorOf(
		ClusterSingletonManager.props(
			alMaxDriver.props,
			PoisonPill,
			ClusterSingletonManagerSettings(context.system)
				.withRole(Some("splitmaster"))
				.withSingletonName(alMaxDriver.name)
		), name = "singleton-master"
	)

	val driver = context.system.actorOf(
		ClusterSingletonProxy.props(
			singletonManager.path.toStringWithoutAddress,
			ClusterSingletonProxySettings(context.system)
				.withRole(Some("splitmaster"))
				.withSingletonName("driver-actor")
		), "driver-actor"
	)

	def receive = {
		case cmd: finish_split_excel_job => driver forward cmd
		case cmd: push_max_job => driver forward cmd
		case _ => ???
	}
}
