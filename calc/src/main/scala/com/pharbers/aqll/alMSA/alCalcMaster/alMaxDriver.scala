package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alScpQueueActor.ExcuteScanScpQueue
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.{alGeneratePanelQueueTrait, alMaxDriverTrait, alScpQueueTrait}
import com.pharbers.aqll.alStart.alHttpFunc.{alUpBeforeItem, alUploadItem}

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

		case _ => ???
	}
}
