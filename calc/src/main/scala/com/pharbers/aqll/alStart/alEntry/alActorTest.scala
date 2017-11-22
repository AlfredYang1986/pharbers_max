package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.pushGeneratePanelJobs
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.pushCalcYMJobs
import com.pharbers.aqll.alStart.alHttpFunc.{alPanelItem, alUploadItem}
import com.typesafe.config.ConfigFactory

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)

	//		 a ! push_filter_job("config/FileBase/201705/CPA_GYCX_panel_201705Urology.xlsx", cp)

	//test generter panel
	if (system.settings.config.getStringList("akka.cluster.roles").contains("splittest")) Cluster(system).registerOnMemberUp {
		val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
		val cpa_file_local = "1705 CPA.xlsx"
		val gycx_file_local = "1705 GYC.xlsx"
		a ! pushCalcYMJobs(alPanelItem("fea9f203d4f593a96f0d6faa91ba24ba", "user", cpa_file_local, gycx_file_local))
	}

	//test calc ym
//	if(system.settings.config.getStringList("akka.cluster.roles").contains("splittest")) Cluster(system).registerOnMemberUp {
//        val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
//		val cpa_file_local = "1705 CPA.xlsx"
//		val gycx_file_local = "1705 GYC.xlsx"
//		a ! pushCalcYMJobs(alPanelItem("fea9f203d4f593a96f0d6faa91ba24ba", "user", cpa_file_local, gycx_file_local))
//    }
}
