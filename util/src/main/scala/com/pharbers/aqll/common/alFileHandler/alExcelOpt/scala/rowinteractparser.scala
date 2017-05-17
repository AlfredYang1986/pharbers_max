package com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala

import akka.actor.ActorRef

case class excelresult(d: BaseExcel)

trait rowinteractparser extends interactparser {
	override def handleOneTarget(target : target_type) = a ! target
}

case class row_exceldataparser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef, targetHandle: BaseExcel) extends rowinteractparser {
	type target_type = BaseExcel
	override def targetInstance = targetHandle
	override def handleOneTarget(target : target_type) = a ! excelresult(target)
}