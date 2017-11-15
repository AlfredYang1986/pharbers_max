package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alScpQueueActor.ExcuteScanScpQueue
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.{alCalcYMTrait, alGeneratePanelTrait, alMaxDriverTrait, alScpQueueTrait}
import com.pharbers.aqll.alStart.alHttpFunc.{alUpBeforeItem, alUploadItem}
import com.pharbers.aqll.common.alFileHandler.fileConfig._

object alMaxDriver {
	def props = Props[alMaxDriver]
	def name = "portion-actor"

	case class pushCalcYMJobs(item : alUpBeforeItem)
	case class pushGeneratePanelJobs(item : alUploadItem)
	case class calcYMSchedule()
	case class generatePanelSchedule()
	case class releaseCalcYMEnergy()
	case class calcYMResult(ym: String)
	case class generatePanelResult(paths: String)
}

class alMaxDriver extends Actor with ActorLogging
								with alMaxDriverTrait
								with alGeneratePanelTrait
								with alCalcYMTrait
								with alScpQueueTrait {

	import alMaxDriver._

	override def receive: Receive = {
		case push_filter_job(file, cp) => println("Start Filter panel文件位置 = " + file); push_filter_job_impl(file, cp)
		case max_calc_done(mp) => max_calc_done_impl(mp)

		case pushGeneratePanelJobs(item) => push_generate_panel_jobs(item, sender)
		case generatePanelSchedule() => generate_panel_schedule_jobs
		case generatePanelResult(panelLst) => println(s"panelLst = ${panelLst}")

		case pushCalcYMJobs(item) => push_calc_ym_jobs(item, sender)
		case calcYMSchedule() => calc_ym_schedule_jobs
		case releaseCalcYMEnergy() => release_calcYM_energy
		case calcYMResult(ym) => println(s"calcYM = ${ym}")

		case ExcuteScanScpQueue() => scanQueue()

		case _ => ???
	}
}
