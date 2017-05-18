package com.pharbers.aqll.alStart.alMaxNode

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alCalaHelp.clusterListenerConfig._
import com.pharbers.aqll.alCalc.almain.{alCalcActor, alGroupActor}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register, worker_register}
import com.pharbers.aqll.alCalcOther.alSchedulerJobs.{alScheduleRemoveFiles, rmFile}
import com.typesafe.config.ConfigFactory
/**
  * Created by qianpeng on 2017/5/18.
  */
object alMaxNode_1 extends App {
	val config = ConfigFactory.load("split-worker_1")
	val system = ActorSystem("calc", config)

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
			//			if(!FileOpt("/Users/qianpeng/Desktop/scp").isDir) FileOpt("/Users/qianpeng/Desktop/scp").createDir
			import scala.concurrent.duration._
			import scala.concurrent.ExecutionContext.Implicits.global
			val a = system.actorSelection(singletonPaht)
			val c = system.actorOf(alCalcActor.props)
			val w = system.actorOf(alGroupActor.props)
			a ! group_register(w)
			a ! calc_register(c)
			a ! worker_register()
			val rm = system.actorOf(alScheduleRemoveFiles.props)
			system.scheduler.schedule(0 seconds, 10 seconds, rm, new rmFile())

			//			val company = "098f6bcd4621d373cade4e832627b4f6"
			//			val filename = "CPA_GYCX_Others_panel.xlsx"
			//			val uname = "testaaa"
			//			val path = fileBase + company + outPut + filename
			//			a ! filter_excel_jobs(path, new alCalcParmary(company, uname), a)
		}
	}
}
