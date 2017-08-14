package com.pharbers.aqll.alMSA.alCalcMaster

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoCalcData.calc_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoFilterExcel.filter_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoGroupData.group_data_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoMaxDriver._
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alCameoSplitExcel.split_excel_end
import com.pharbers.aqll.alMSA.alCalcMaster.alMasterTrait.alMaxDriverTrait

object alMaxDriver {
	def props = Props[alMaxDriver]
	def name = "portion-actor"
}

class alMaxDriver extends Actor with ActorLogging
								with alMaxDriverTrait{
	
	override def receive: Receive = {
		case push_filter_job(file, cp) => push_filter_job_impl(file, cp)
		case _ => ???
	}
}
