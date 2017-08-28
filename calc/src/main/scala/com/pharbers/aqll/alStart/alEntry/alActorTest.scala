package com.pharbers.aqll.alStart.alEntry

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.pharbers.aqll.alCalaHelp.alMaxDefines.{alCalcParmary, alMaxProperty}
import com.pharbers.aqll.alCalcMemory.aljobs.aljobtrigger.alJobTrigger.{push_calc_job_2, push_group_job, push_split_excel_job}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver.push_filter_job
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end
import com.pharbers.aqll.common.alFileHandler.fileConfig.{Upload_Secondstep_Filename, fileBase, program, root}
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_end
import com.pharbers.aqll.common.alCmd.pycmd.pyCmd

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Await

object alActorTest extends App {
	val config = ConfigFactory.load("split-test")
	val system : ActorSystem = ActorSystem("calc", config)
	val cp = new alCalcParmary("fea9f203d4f593a96f0d6faa91ba24ba", "jeorch")
	implicit val timeout = Timeout(30 minute)

	if(system.settings.config.getStringList("akka.cluster.roles").contains("splittest")) {
		Cluster(system).registerOnMemberUp {
			val a = system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/portion-actor")
			val path = fileBase + "2016-11.xlsx"

			a ! push_filter_job(path, cp)

//			val f = a ? push_split_excel_job(path, cp)
//			val r = Await.result(f, 2 minute).asInstanceOf[split_excel_end]
//
//			val p = r.uuid
//			val subs = r.subs map (x => alMaxProperty(p, x, Nil))
//			val mp = alMaxProperty(null, p, subs)
//
//			cp.uuid = r.uuid
//
//			val fg = a ? push_group_job(mp)
//			val rg = Await.result(fg, 2 minute).asInstanceOf[group_data_end]
//
//			val fff = a ? push_calc_job_2(mp, cp)
//			val rrr = Await.result(fff, 40 minute).asInstanceOf[calc_data_end]
//
//	        println(rrr.property.finalValue)
//	        println(rrr.property.finalUnit)
		}
	}
	
//	pyCmd(s"~/FileBase/fea9f203d4f593a96f0d6faa91ba24ba",Upload_Secondstep_Filename, "2016#").excute
}
