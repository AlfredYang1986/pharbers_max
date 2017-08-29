package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alMaxDriverTrait

object alMaxDriver {
	def props = Props[alMaxDriver]
	def name = "portion-actor"
}

class alMaxDriver extends Actor with ActorLogging
								with alMaxDriverTrait{
	
	override def receive: Receive = {
		case push_filter_job(file, cp) => push_filter_job_impl(file, cp)
		case max_calc_done(mp) => max_calc_done_impl(mp)
		case _ => ???
	}
}
