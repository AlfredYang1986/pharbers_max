package com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala

import java.io.FileOutputStream

import akka.actor.ActorRef
import com.pharbers.aqll.common.alFileHandler.alFileHandler

/**
  * Created by qianpeng on 2017/5/11.
  */

abstract class BaseExcel()

class alExcelDataParser(target: BaseExcel, xml_file_name : String, xml_file_name_ch : String) extends alFileHandler with CreateInnerParsers{
	val parser = CreateInnerParser(target, xml_file_name, xml_file_name_ch)
	override def prase(path : String)(x : Any) : Any = {
		parser.startParse(path)
		this
	}
}

class alExcelWriteDataParser[T](lst: List[T], path: String) extends alFileHandler with writeparser[T]{
	override def write: Unit = {
		val out = new FileOutputStream(path)
		getWorkBook(lst).write(out)
		out.flush
		out.close
	}
}

case class inner_parsers(xml_file_name : String, xml_file_name_ch : String, a : ActorRef, h : alFileHandler, targetHandle: BaseExcel) extends rowinteractparser {
	type target_type = BaseExcel
	override def targetInstance: target_type = targetHandle.getClass.newInstance
	override def handleOneTarget(target: target_type) = h.data.append(target)
}

trait CreateInnerParsers { this : alFileHandler =>
	def CreateInnerParser(target: BaseExcel, xml_file_name : String, xml_file_name_ch : String) : inner_parsers =
		new inner_parsers(xml_file_name, xml_file_name_ch, null, this, target)
}