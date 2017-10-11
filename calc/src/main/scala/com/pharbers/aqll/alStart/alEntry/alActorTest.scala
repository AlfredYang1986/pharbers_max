package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.pushGeneratePanelJobs
import com.pharbers.aqll.alStart.alHttpFunc.alUploadItem
import com.typesafe.config.ConfigFactory

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)

	if(system.settings.config.getStringList("akka.cluster.roles").contains("splittest")) {
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")

			val path = "config/FileBase/fea9f203d4f593a96f0d6faa91ba24ba/Client"
			val cpa_file_local = path + "/CPA/1705 CPA.xlsx"
			val gycx_file_local = path + "/GYCX/1705 GYC.xlsx"
			(1 to 10).foreach {_ =>
				a ! pushGeneratePanelJobs(alUploadItem("fea9f203d4f593a96f0d6faa91ba24ba", "user", cpa_file_local, gycx_file_local, "201705"))
			}
		}
	}
}