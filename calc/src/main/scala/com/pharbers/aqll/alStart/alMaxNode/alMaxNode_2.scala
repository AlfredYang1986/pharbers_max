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
object alMaxNode_2 extends App{
	val config = ConfigFactory.load("split-worker_2")
	val system = ActorSystem("calc", config)

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
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
			//			a ! push_max_job("""config/new_test/AI_R_panel 201501.xlsx""")
			//a ! filter_excel_jobs("""config/new_test/2016-01.xlsx""", new alCalcParmary("IMS"), a)
			//			a ! filter_excel_jobs("""config/new_test/CPA_GYCX_panel_2016Specialty.xlsx""", new alCalcParmary("BMS"), a)
			//a ! push_max_job("""config/new_test/CPA_GYCX_panel_2016Specialty.xlsx""", new alCalcParmary("BMS"))
		}
	}
}
