package com.pharbers.aqll.alStart.alEntry

import java.io.File

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alMSA.alCalcMaster.alMaxDriver.pushGeneratePanelJobs
import com.pharbers.aqll.alStart.alHttpFunc.alUploadItem
import com.typesafe.config.ConfigFactory
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver._

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)
	val cp = new alCalcParmary("fea9f203d4f593a96f0d6faa91ba24ba", "jeorch")

	if(system.settings.config.getStringList("akka.cluster.roles").contains("splittest")) Cluster(system).registerOnMemberUp {
        val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")

		// val calc_path = "config/FileBase/201705oversize"
		// val file: File = new File(calc_path)
		// val fileList: Array[File] = file.listFiles()
		// println(s"=== files count = ${fileList.length} ===")
		// fileList.foreach(x => {
		// 	println(s"=== file = ${x} ===")
		// 	1 to 1 foreach (i => a ! push_filter_job(x.toString, cp))
		// })

//		a ! push_filter_job("config/FileBase/201705/CPA_GYCX_panel_201705DVP.xlsx", cp)
//		 a ! push_filter_job("config/FileBase/2017-02.xlsx", cp)

//		 a ! push_filter_job("config/FileBase/201705AI_R.xlsx", cp)

       val cpa_file_local = "/CPA/1705 CPA.xlsx"
       val gycx_file_local = "/GYCX/1705 GYC.xlsx"


		1 to 1 foreach (x => {
			a ! pushGeneratePanelJobs(alUploadItem("fea9f203d4f593a96f0d6faa91ba24ba", "user", cpa_file_local, gycx_file_local, "201705" :: Nil))
		})

    }
}