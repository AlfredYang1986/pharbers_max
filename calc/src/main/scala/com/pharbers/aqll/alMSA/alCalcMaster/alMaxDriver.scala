package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alScpQueueActor.ExcuteScanScpQueue
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.{alGeneratePanelQueueTrait, alMaxDriverTrait, alScpQueueTrait}
import com.pharbers.aqll.alStart.alHttpFunc.{alUpBeforeItem, alUploadItem}
import com.pharbers.aqll.common.alFileHandler.fileConfig.fileBase

object alMaxDriver {
	def props = Props[alMaxDriver]
	def name = "portion-actor"

	case class pushCalcYMJobs(item : alUpBeforeItem)
	case class pushGeneratePanelJobs(item : alUploadItem)
	case class calcYMSchedule()
	case class generatePanelSchedule()
	case class calcYMJob(item : alUpBeforeItem)
	case class generatePanelJob(item : alUploadItem)
	case class releasePyEnergy()
	case class calcYMResult(ym:String)
	case class generatePanelResult(file_name:String)
}

class alMaxDriver extends Actor with ActorLogging
								with alMaxDriverTrait
								with alGeneratePanelQueueTrait
								with alScpQueueTrait {

	import alMaxDriver._

	override def receive: Receive = {
		case push_filter_job(file, cp) => push_filter_job_impl(file, cp)
		case max_calc_done(mp) => max_calc_done_impl(mp)

		case pushCalcYMJobs(item) => push_calc_ym_jobs(item)
		case pushGeneratePanelJobs(item) => push_generate_panel_jobs(item)
		case calcYMSchedule() => calc_ym_schedule_jobs
		case generatePanelSchedule() => generate_panel_schedule_jobs
		case releasePyEnergy() => release_py_energy
		case ExcuteScanScpQueue() => scanQueue()
		case calcYMResult(ym) => sender ! calcYMResult(ym)
		case generatePanelResult(file_name) => {
			val cp = new alCalcParmary("fea9f203d4f593a96f0d6faa91ba24ba", "jeorch")
			println("panel文件位置 = " + fileBase + file_name)
			self ! push_filter_job(fileBase + file_name,cp)
		}

		case _ => ???
	}
}
