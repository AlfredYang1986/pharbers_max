package com.pharbers.aqll.calc.split

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.excel.core.integratedresult
import com.pharbers.aqll.calc.maxmessages.excelJobStart
import com.pharbers.aqll.calc.util.Const
import com.pharbers.aqll.calc.util.export.BeanToExcel

import scala.collection.mutable.ArrayBuffer

/**
  * Created by qianpeng on 2017/2/21.
  */
object SplitExcelWorker {
	def props(b: SplitEventBus, map: Map[String, Any]) = Props(new SplitExcelWorker(b, map))
}

class SplitExcelWorker(bus: SplitEventBus, m: Map[String, Any]) extends Actor with ActorLogging with CreateSplitExcelWorker {

	import collection.JavaConversions._
	bus.subscribe(self, "AggregorBus")

	var num = 0
	val data: ArrayBuffer[IntegratedData] = ArrayBuffer.empty
	val split: Receive = {
		case integratedresult(target) => {
			data ++= (target :: Nil)
			num += 1
			num match {case Const.SPLITEXCEL => {creatFile; num = 0} case _ => Unit}
		}

		case SplitEventBus.excelEnded(map) => {
			println(s"read split ended at $self")
			creatFile
			context.stop(self)
		}
		case _ => Unit
	}

	def receive = split

	def creatFile = {
		val path = Const.OUTFILE + UUID.randomUUID
		val map = m.map { x => x._1 match {case "filename" => (x._1, path) case _ => x}}
		BeanToExcel.writeToFile(data.toList, null, path)
		data.clear()
//		context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/splitreception") ! excelJobStart(map)
	}
}
