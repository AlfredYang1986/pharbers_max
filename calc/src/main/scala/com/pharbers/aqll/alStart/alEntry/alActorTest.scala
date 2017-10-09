package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.pushGeneratePanelJobs
import com.pharbers.aqll.alStart.alHttpFunc.alUploadItem

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)

	if(system.settings.config.getStringList("akka.cluster.roles").contains("splittest")) {
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")

			val gycx_file_local = "/home/clock/workSpace/blackMirror/dependence/program/generatePanel/file/Client/GYCX/1705 GYC.xlsx"
			val cpa_file_local = "/home/clock/workSpace/blackMirror/dependence/program/generatePanel/file/Client/CPA/1705 CPA.xlsx"

			a ! pushGeneratePanelJobs(alUploadItem("generatePanel", "user", cpa_file_local, gycx_file_local, "201705"))
		}
	}
}