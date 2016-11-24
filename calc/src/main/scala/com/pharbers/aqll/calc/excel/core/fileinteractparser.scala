package com.pharbers.aqll.calc.excel.core

import akka.actor.ActorRef

trait fileinteractparser extends interactparser {
	override def handleOneTarget(target : target_type) = Unit
}

case class hospinteractparser(xml_file_name : String, xml_file_name_ch : String) extends fileinteractparser {
	override val a : ActorRef = null
	type target_type = com.pharbers.aqll.calc.excel.Manage.AdminHospitalDataBase
	override def targetInstance = new target_type
}

// TODO : 有多少加多少