package com.pharbers.aqll.stub

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{calc_register, filter_excel_jobs, group_register, push_max_job}
import com.pharbers.aqll.alcalc.almain.{alCalcActor, alGroupActor}
import com.pharbers.aqll.alcalc.almaxdefines.alCalcParmary
import com.pharbers.aqll.util.GetProperties
import com.typesafe.config.ConfigFactory

/**
  * Created by qianpeng on 2017/3/11.
  */
object stub_test_5 extends App{
	val config = ConfigFactory.load("split-worker_2")
	val system = ActorSystem("calc", config)

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection(GetProperties.singletonPaht)
			val c = system.actorOf(alCalcActor.props)
			val w = system.actorOf(alGroupActor.props)
//			if(!FileOpt("/Users/qianpeng/Desktop/scp").isDir) FileOpt("/Users/qianpeng/Desktop/scp").createDir
			a ! group_register(w)
			a ! calc_register(c)
//			a ! push_max_job("""config/new_test/AI_R_panel 201501.xlsx""")
			a ! filter_excel_jobs("""config/new_test/2016-01.xlsx""", new alCalcParmary("IMS"), a)
//			a ! filter_excel_jobs("""config/new_test/CPA_GYCX_panel_2016Specialty.xlsx""", new alCalcParmary("BMS"), a)
			//a ! push_max_job("""config/new_test/CPA_GYCX_panel_2016Specialty.xlsx""", new alCalcParmary("BMS"))
		}
	}
}
