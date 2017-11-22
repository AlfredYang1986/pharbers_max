package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcStep, alMaxRunning}
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxMaster.{pushCalcYMJob, pushSplitPanelJob, pushGeneratePanelJob}
import com.pharbers.aqll.alStart.alHttpFunc.alPanelItem
import com.typesafe.config.ConfigFactory

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)
	val excel_panel = "config/FileBase/201706/CPA_GYCX_panel_201706INF.xlsx"
	val csv_panel = "config/FileBase/fea9f203d4f593a96f0d6faa91ba24ba/Output/0c6d8a01-4603-4c81-967f-28446c46d34c"

	//test split -> group -> calc -> bson
	if (false && system.settings.config.getStringList("akka.cluster.roles").contains("splittest")){
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
			a ! pushSplitPanelJob(alMaxRunning("uid","rid","parent","ym","mkt",csv_panel,Nil,alCalcStep().PANEL))
		}
	}


	//test generter panel
	if (system.settings.config.getStringList("akka.cluster.roles").contains("splittest")){
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
			val cpa_file_local = "1705 CPA.xlsx"
			val gycx_file_local = "1705 GYC.xlsx"
			a ! pushGeneratePanelJob(alPanelItem("fea9f203d4f593a96f0d6faa91ba24ba", "user", cpa_file_local, gycx_file_local, List("201705")))
			println("test generter panel")
		}
	}

	//test calc ym
	if(system.settings.config.getStringList("akka.cluster.roles").contains("splittest")){
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/driver-actor")
			val cpa_file_local = "1705 CPA.xlsx"
			val gycx_file_local = "1705 GYC.xlsx"
			a ! pushCalcYMJob(alPanelItem("fea9f203d4f593a96f0d6faa91ba24ba", "user", cpa_file_local, gycx_file_local))
		}
	}
}
