package com.pharbers.aqll.stub

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register, push_max_job, worker_register}
import com.pharbers.aqll.alcalc.almain.{alCalcActor, alDriverSingleton, alGroupActor, alMaxDriver}
import com.pharbers.aqll.util.GetProperties
import com.typesafe.config.ConfigFactory

/**
  * Created by qianpeng on 2017/3/11.
  */
object stub_test_4 extends App{
	val config = ConfigFactory.load("split-worker_1")
	val system = ActorSystem("calc", config)

	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection(GetProperties.singletonPaht)
			val c = system.actorOf(alCalcActor.props)
			val w = system.actorOf(alGroupActor.props)
			//if(!FileOpt("/Users/qianpeng/Desktop/scp").isDir) FileOpt("/Users/qianpeng/Desktop/scp").createDir
			a ! group_register(w)
			a ! calc_register(c)
//			a ! push_max_job("""config/new_test/2016-01.xlsx""")
//			a ! push_max_job("""config/new_test/AI_R_panel 201501.xlsx""")
		}
	}
}
