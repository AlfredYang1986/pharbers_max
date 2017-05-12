package com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala

import akka.actor.ActorRef
import com.pharbers.aqll.common.alFileHandler.alFileHandler

/**
  * Created by qianpeng on 2017/5/11.
  */

abstract class BaseExcle()

class alExcelDataParser(target: BaseExcle, xml_file_name : String, xml_file_name_ch : String) extends alFileHandler with CreateInnerParsers{
	val parser = CreateInnerParser(target, xml_file_name, xml_file_name_ch)
	override def prase(path : String)(x : Any) : Any = {
		parser.startParse(path)
		this
	}
}

case class inner_parsers(xml_file_name : String, xml_file_name_ch : String, a : ActorRef, h : alFileHandler, targetHandle: BaseExcle) extends rowinteractparser {
	type target_type = BaseExcle
	override def targetInstance: target_type = targetHandle
	override def handleOneTarget(target: target_type) = h.data.append(target)
}

trait CreateInnerParsers { this : alFileHandler =>
	def CreateInnerParser(target: BaseExcle, xml_file_name : String, xml_file_name_ch : String) : inner_parsers =
		new inner_parsers(xml_file_name, xml_file_name_ch, null, this, target)
}