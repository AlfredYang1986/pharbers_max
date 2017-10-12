package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.pushGeneratePanelJobs
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.push_filter_job
import com.pharbers.aqll.alStart.alHttpFunc.alUploadItem
import com.typesafe.config.ConfigFactory
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)
	val cp = new alCalcParmary("fea9f203d4f593a96f0d6faa91ba24ba", "jeorch")

	if(system.settings.config.getStringList("akka.cluster.roles").contains("splittest")) {
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")

			val calc_path = "config/FileBase/fea9f203d4f593a96f0d6faa91ba24ba/Output/a.xlsx"

			val path = "config/FileBase/fea9f203d4f593a96f0d6faa91ba24ba/Client"
			val cpa_file_local = path + "/CPA/1705 CPA.xlsx"
			val gycx_file_local = path + "/GYCX/1705 GYC.xlsx"
			(1 to 5).foreach {_ =>
				a ! pushGeneratePanelJobs(alUploadItem("fea9f203d4f593a96f0d6faa91ba24ba", "user", cpa_file_local, gycx_file_local, "201705"))
			}
		}
	}
}