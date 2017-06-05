package com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala

import akka.actor.ActorRef

trait fileinteractparser extends interactparser {
	override def handleOneTarget(target : target_type) = Unit
}

case class exceldataparser(xml_file_name: String, xml_file_name_ch: String, targetHandle: BaseExcel) extends fileinteractparser {
	override val a : ActorRef = null
	type target_type = BaseExcel
	override def targetInstance = targetHandle.getClass.newInstance
}