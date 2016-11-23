package com.pharbers.aqll.calc.excel.core

import akka.actor.ActorRef

case class rowinteractparser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef) extends interactparser {
	override def handleOneTarget(target : target_type) = a ! target
}