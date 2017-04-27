package com.pharbers.aqll.alcalc.almain

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger._

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
		case cmd : db_final_result => driver forward cmd
		case cmd : filter_excel_jobs => driver forward cmd
		case cmd : finish_split_excel_job => driver forward cmd
		case cmd : push_max_job => driver forward cmd
		case cmd : group_register => driver forward cmd
		case cmd : group_result => driver forward cmd
		case cmd : calc_register => driver forward cmd
		case cmd : push_calc_job => driver forward cmd
		case cmd : calc_sum_result => driver forward cmd
		case cmd : calc_final_result => driver forward cmd
		case cmd : commit_finalresult_jobs => driver forward cmd
		case cmd : check_excel_jobs => driver forward cmd
		case cmd : worker_register => driver forward cmd
		case cmd : crash_calc => driver forward cmd
		case cmd : crash_group => driver forward cmd
		case _ => ???
	}
}
