package com.pharbers.aqll.old.calc.stub

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.old.calc.alcalc.aljobs.aljobtrigger.alJobTrigger._
import com.pharbers.aqll.old.calc.alcalc.almain.{alCalcActor, alDriverSingleton, alGroupActor}
import com.pharbers.aqll.old.calc.alcalc.almaxdefines.alCalcParmary
import com.pharbers.aqll.old.calc.util.GetProperties
import com.typesafe.config.ConfigFactory

/**
  * Created by qianpeng on 2017/3/11.
  */
object stub_test_5 extends App{
	val config = ConfigFactory.load("split-worker_2")
	val system = ActorSystem("calc", config)

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
			import scala.concurrent.duration._
			import scala.concurrent.ExecutionContext.Implicits.global
			import com.pharbers.aqll.old.calc.alcalc.alSchedulerJobs.{alScheduleRemoveFiles, rmFile}
			val a = system.actorSelection(GetProperties.singletonPaht)
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
