package com.pharbers.aqll.calc.split

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import akka.routing.RoundRobinPool
import com.pharbers.aqll.calc.common.DefaultData.integratedXmlPath
import com.pharbers.aqll.calc.excel.core.row_integrateddataparser
import com.pharbers.aqll.calc.maxmessages.{excelJobStart, excelSplitStart}

sealed trait MsaterSplitState

case object MsaterSplitExcelIdel extends MsaterSplitState

// 分拆Excel文件
case object MsaterSplitExcelStart extends MsaterSplitState

//开始拆分Excel文件
case object MsaterSplitExcelEnd extends MsaterSplitState

//分拆Excel文件结束
case class MsaterSplitStateData(var fileName: String, var getcompany: String, var subFileName: String)


case class split_excel_start(map: Map[String, Any])

case class split_excel_end()//map: Map[String, Any]

case class split_excel_processing(map: Map[String, Any])

case class split_excel_resultdata(result: List[(String, List[String])])

/**
  * Created by qianpeng on 2017/2/21.
  */
object SplitExcel {
	def props = Props[SplitExcel]

	val num_count = 1
}

class SplitExcel extends Actor with ActorLogging
	with CreateSplitExcelWorker
	with CreateSplitExcelEventBus
	with FSM[MsaterSplitState, MsaterSplitStateData] {
	var originSender : ActorRef = null
	startWith(MsaterSplitExcelIdel, new MsaterSplitStateData("", "", ""))

	when(MsaterSplitExcelIdel) {
		case Event(excelSplitStart(map), data) => {
			data.getcompany = map.get("company").get.toString
			data.fileName = map.get("filename").get.toString
			println("join splitExcel")
			originSender = sender()
			self ! split_excel_start(map)
			goto(MsaterSplitExcelStart) using data
		}
	}

	when(MsaterSplitExcelStart) {
		case Event(split_excel_start(map), data) => {
			val router = CreateSplitExcelWorker(bus, map, self)
			println("join splitStartExcel")
			(map.get("JobDefines").get.asInstanceOf[JobDefines].t match {
				case 4 => {
					row_integrateddataparser(integratedXmlPath.integratedxmlpath_en,
						integratedXmlPath.integratedxmlpath_ch,
						router)
				}
			}).startParse(map.get("filename").get.toString, 1)
			bus.publish(SplitEventBus.excelEnded(map))
			stay
		}
		case Event(split_excel_resultdata(result), data) => {
			originSender ! result
			self ! split_excel_end()
			goto(MsaterSplitExcelEnd) using data
		}
	}

	when(MsaterSplitExcelEnd) {
		case Event(split_excel_end(), data) => {
			goto(MsaterSplitExcelIdel) using data.copy(fileName = "", getcompany = "", subFileName = "")
		}
	}

	whenUnhandled {
		case Event(e, s) => {
			println(s"cannot handle message $e  state is ${this.stateName}")
			stay
		}
	}

	val bus = CreateSplitExcelEventBus

}

trait CreateSplitExcelWorker {
	this: Actor =>
	def CreateSplitExcelWorker(a: SplitEventBus, map: Map[String, Any], master: ActorRef) = {
		context.actorOf(RoundRobinPool(1).props(SplitExcelWorker.props(a, map, master)), name = "worker-split-router")
	}
}

trait CreateSplitExcelEventBus {
	this: Actor =>
	def CreateSplitExcelEventBus = new SplitEventBus(SplitExcel.num_count)
}