package com.pharbers.aqll.calc.split

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.excel.core.integratedresult
import com.pharbers.aqll.calc.maxmessages.excelJobStart
import com.pharbers.aqll.calc.util.Const
import com.pharbers.aqll.calc.util.export.BeanToExcel

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.stm.{Ref, atomic}

/**
  * Created by qianpeng on 2017/2/21.
  */
object SplitExcelWorker {
	def props(b: SplitEventBus, map: Map[String, Any], m: ActorRef) = Props(new SplitExcelWorker(b, map, m))
}

class SplitExcelWorker(bus: SplitEventBus, m: Map[String, Any], master: ActorRef) extends Actor with ActorLogging with CreateSplitExcelWorker {

	import collection.JavaConversions._
	bus.subscribe(self, "AggregorBus")
//	val filedata = Ref(List[(String, List[String])]())
	var num = 0
	val data: ArrayBuffer[IntegratedData] = ArrayBuffer.empty
	val split: Receive = {
		case integratedresult(target) => {
			data ++= (target :: Nil)
			num += 1
			num match {
				case Const.SPLITEXCEL =>
					val r = creatFile
					master ! split_excel_resultdata(r)
				case _ => Nil
			}
		}

		case SplitEventBus.excelEnded(map) => {
			println(s"read split ended at $self")
			val r = creatFile
			master ! split_excel_resultdata(r)

//			atomic { implicit thx =>
//				val data = (filedata.single.get.map(_._1).distinct, filedata.single.get.map(_._2).flatten)
//				println(data)
//			}

			context.stop(self)
		}
		case _ => Unit
	}

	def receive = split

	def creatFile: List[(String, List[String])] = {
		atomic{ implicit thx =>
			val f = m.get("filename")
			num = 0
			val path = Const.OUTFILE + UUID.randomUUID
			try {
				BeanToExcel.writeToFile(data.toList, null, path)
			}catch {
				case ex: Exception => println(ex)
			}
			data.clear()
			List((f.get.toString, List(path)))
		}
		//context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/splitreception") ! excelJobStart(map)
	}
}
