package com.pharbers.aqll.stub

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alcalc.aljobs.aljobtrigger.alJobTrigger.{push_max_job, worker_register}
import com.pharbers.aqll.alcalc.almain.alMaxDriver
import com.typesafe.config.ConfigFactory
import com.pharbers.aqll.alcalc.almain.alDriverSingleton

/**
  * Created by qianpeng on 2017/3/11.
  */
object stub_test_4 extends App{
	val config = ConfigFactory.load("split-worker_1")
	val system = ActorSystem("calc", config)

	val a = system.actorOf(alDriverSingleton.props, alDriverSingleton.name)
	if (system.settings.config.getStringList("akka.cluster.roles").contains("splitworker")) {
		Cluster(system).registerOnMemberUp {
//			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/splitreception")
//			val driver = system.actorOf(alMaxDriver.props, "split-master")
//			val map = Map("CPU" -> "4", "Memory" -> "8G")
//			a.tell(new worker_register(map), driver)
			//a ! push_max_job("""config/new_test/2016-01.xlsx""")
			val driver = system.actorOf(alMaxDriver.props, "split-master")
			driver ! push_max_job("""config/new_test/2016-01.xlsx""")
		}
	}
}
