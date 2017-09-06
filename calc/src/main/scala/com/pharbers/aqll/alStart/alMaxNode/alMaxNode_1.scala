package com.pharbers.aqll.alStart.alMaxNode

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alCalcEnergy.{alCalcRegisterActor, alGroupRegisterActor}
import com.pharbers.aqll.alCalcOther.alRemoveJobs.{alScheduleRemoveFiles, rmFile}
import com.typesafe.config.ConfigFactory

/**
  * Created by qianpeng on 2017/5/18.
  */
object alMaxNode_1 extends App {
	val config = ConfigFactory.load("split-worker_1")
	val system = ActorSystem("calc", config)
	
	system.actorOf(alGroupRegisterActor.props, "registergroup")
	system.actorOf(alCalcRegisterActor.props, "registercalc")
	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
			import scala.concurrent.duration._
			import scala.concurrent.ExecutionContext.Implicits.global
			val rm = system.actorOf(alScheduleRemoveFiles.props)
			system.scheduler.schedule(0 seconds, 10 seconds, rm, new rmFile())
		}
	}
}
