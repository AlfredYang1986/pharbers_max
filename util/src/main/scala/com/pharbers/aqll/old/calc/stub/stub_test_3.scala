package com.pharbers.aqll.old.calc.stub

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.pharbers.aqll.old.calc.alcalc.aljobs.aljobtrigger.alJobTrigger.{calc_register, group_register, push_max_job, worker_register}
import com.pharbers.aqll.old.calc.alcalc.almain.{alCalcActor, alDriverSingleton, alGroupActor, alMaxDriver}
import com.pharbers.aqll.old.calc.util.GetProperties
import com.typesafe.config.ConfigFactory

/**
  * Created by Alfred on 10/03/2017.
  */

// test cases : 分布式Masterces

object stub_test_3 extends App {
    val config = ConfigFactory.load("split-master")
    val system = ActorSystem("calc", config)
    if(system.settings.config.getStringList("akka.cluster.roles").contains("splitmaster")) {
        Cluster(system).registerOnMemberUp {
            println("cluster ready")
            import scala.concurrent.duration._
            import scala.concurrent.ExecutionContext.Implicits.global
            val w = system.actorOf(alGroupActor.props)
            val c = system.actorOf(alCalcActor.props)
//            val a = system.actorSelection(GetProperties.singletonPaht)
            val a = system.actorOf(alDriverSingleton.props)
            a ! group_register(w)
            a ! calc_register(c)
            system.scheduler.scheduleOnce(20 second, a, worker_register())
//            a ! worker_register()
//            a ! push_max_job("""config/new_test/2016-01.xlsx""")
//            a ! push_max_job("""config/new_test/AI_R_panel 201501.xlsx""")
        }
    }
}