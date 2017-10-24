package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alCalaHelp.alMaxDefines.alCalcParmary
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alScpQueueActor.ExcuteScanScpQueue
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.{alCalcYMTrait, alGeneratePanelTrait, alMaxDriverTrait, alScpQueueTrait}
import com.pharbers.aqll.alStart.alHttpFunc.{alUpBeforeItem, alUploadItem}

object alMaxDriver {
	def props = Props[alMaxDriver]
	def name = "portion-actor"

	case class pushCalcYMJobs(item : alUpBeforeItem)
	case class pushGeneratePanelJobs(item : alUploadItem)
	case class calcYMSchedule()
	case class generatePanelSchedule()
	case class releasePanelEnergy()
	case class releaseCalcYMEnergy()
	case class calcYMResult(ym : String)
	case class generatePanelResult(paths : List[String])
}

class alMaxDriver extends Actor with ActorLogging
								with alMaxDriverTrait
								with alGeneratePanelTrait
								with alCalcYMTrait
								with alScpQueueTrait {

	import alMaxDriver._

	override def receive: Receive = {
		case push_filter_job(file, cp) => push_filter_job_impl(file, cp)
		case max_calc_done(mp) => max_calc_done_impl(mp)

		case pushGeneratePanelJobs(item) => push_generate_panel_jobs(item, sender)
		case generatePanelSchedule() => generate_panel_schedule_jobs
		case releasePanelEnergy() => release_panel_energy
		case generatePanelResult(paths) => {
			val cp = new alCalcParmary("fea9f203d4f593a96f0d6faa91ba24ba", "jeorch")
			paths.foreach {x =>
				println("panel文件位置 = " + x)
				self ! push_filter_job(x, cp)
			}
		}

		case pushCalcYMJobs(item) => push_calc_ym_jobs(item, sender)
		case calcYMSchedule() => calc_ym_schedule_jobs
		case releaseCalcYMEnergy() => release_calcYM_energy
		case ExcuteScanScpQueue() => scanQueue()
		case calcYMResult(ym) => log.info(s"calcYM=${ym}")//sender ! calcYMResult(ym)

		case _ => ???
	}
}
