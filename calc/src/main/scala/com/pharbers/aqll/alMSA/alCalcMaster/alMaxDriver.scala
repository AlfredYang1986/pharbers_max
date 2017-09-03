package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.{alMaxDriverTrait, alPyQueueTrait}
import com.pharbers.aqll.alStart.alHttpFunc.{alUpBeforeItem, alUploadItem}

object alMaxDriver {
	def props = Props[alMaxDriver]
	def name = "portion-actor"

	case class pushPyUbJobs(item : alUpBeforeItem)
	case class pushPyUlJobs(item : alUploadItem)
	case class pyUbSchedule()
	case class pyUlSchedule()
	case class doPyUbJob(item : alUpBeforeItem)
	case class doPyUlJob(item : alUploadItem)
}

class alMaxDriver extends Actor with ActorLogging
								with alMaxDriverTrait
								with alPyQueueTrait{

	import alMaxDriver._

	override def receive: Receive = {
		case push_filter_job(file, cp) => push_filter_job_impl(file, cp)
		case max_calc_done(mp) => max_calc_done_impl(mp)

		case pushPyUbJobs(item) => push_py_ub_jobs(item)
		case pushPyUlJobs(item) => push_py_ul_jobs(item)
		case pyUbSchedule() => py_ub_schedule_jobs
		case pyUlSchedule() => py_ul_schedule_jobs
		case doPyUbJob(item) => do_py_ub_job(item)
		case doPyUlJob(item) => do_py_ul_job(item)

		case _ => ???
	}
}
