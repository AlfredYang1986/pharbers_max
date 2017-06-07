package com.pharbers.aqll.alStart.alMaxNode

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.util.Timeout
import com.pharbers.aqll.common.alFileHandler.clusterListenerConfig._
import com.pharbers.aqll.alCalc.almain.{alCalcActor, alGroupActor, alMaxDriver}
import com.pharbers.aqll.alCalcEnergy.{alCalcRegisterActor, alGroupRegisterActor}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register, worker_register}
import com.pharbers.aqll.alCalcOther.alRemoveJobs.{alScheduleRemoveFiles, rmFile}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.util.{Failure, Success}
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
			implicit val timeout = Timeout(5 seconds)
			system.actorSelection(singletonPaht).resolveOne().onComplete {
				case Success(actorRef) =>
					println(s"actorRef = $actorRef")
					system.scheduler.scheduleOnce(10 seconds, actorRef, "test")
				case Failure(ex) => ???
			}
//			val c = system.actorOf(alCalcActor.props)
//			val w = system.actorOf(alGroupActor.props)
//			a ! group_register(w)
//			a ! calc_register(c)
//			a ! worker_register()
			val rm = system.actorOf(alScheduleRemoveFiles.props)
			system.scheduler.schedule(0 seconds, 10 seconds, rm, new rmFile())
		}
	}
}
