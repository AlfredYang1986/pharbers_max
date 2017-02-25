package com.pharbers.aqll.calc.split

import java.io.File

import com.pharbers.aqll.calc.util.{DateUtil, StringOption}
import com.pharbers.aqll.calc.common.DefaultData.integratedXmlPath
import com.pharbers.aqll.calc.excel.core._
import com.pharbers.aqll.calc.maxmessages._
import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import akka.routing.RoundRobinPool
import com.pharbers.aqll.calc.maxresult.Insert
import com.pharbers.aqll.calc.maxresult.InserAdapter
import java.util.Date

import akka.actor.SupervisorStrategy.Restart
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.split.SplitWorker.{integratedresultext, responseaverage}
import com.pharbers.aqll.calc.util.text.FileOperation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.collection.mutable.ArrayBuffer

case class processing_excel(map: Map[String, Any])

case class processing_data(mr: List[(String, (Long, Double, Double, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], String, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)]))])

object SplitMaster {
	def props = Props[SplitMaster]

	val num_count = 10 // magic number
}

sealed trait MsaterState

case object MsaterIdleing extends MsaterState     // 空跑，空闲状态
case object MsaterPreAggCalcing extends MsaterState     // 数据检查和整合
case object MsaterWaitAggreating extends MsaterState    // 数据在内存中等待
case object MsaterPostAggCalcing extends MsaterState    // 数据计算
case object MsaterPrecessingData extends MsaterState    // 数据计算结果写入数据库中
case object MsaterCalcing extends MsaterState     // 计算中，以后细化

case class MsaterStateData(var fileName: String, var getcompany: String, var subFileName: String)

class SplitMaster extends Actor with ActorLogging
	with CreateSplitWorker
	with CreateSplitEventBus
	with CreateSplitAggregator
	with FSM[MsaterState, MsaterStateData] {

	startWith(MsaterIdleing, new MsaterStateData("", "", ""))


	when(MsaterIdleing) {
		case Event(startReadExcel(map), data) => {
			data.getcompany = map.get("company").get.toString
			data.fileName = map.get("filename").get.asInstanceOf[(String, String)]._1
			data.subFileName = map.get("filename").get.asInstanceOf[(String, String)]._2
			self ! processing_excel(map)
			sender ! new canHandling()
			goto(MsaterCalcing) using data
		}
	}

	when(MsaterCalcing) {
		case Event(processing_excel(map), data) => {
//			(map.get("JobDefines").get.asInstanceOf[JobDefines].t match {
//				case 4 => {
//					row_integrateddataparser(integratedXmlPath.integratedxmlpath_en,
//						integratedXmlPath.integratedxmlpath_ch,
//						router)
//				}
//			}).startParse(data.fileName, 1)
			import collection.JavaConversions._
			map.get("JobDefines").get.asInstanceOf[JobDefines].t match {
				case 4 => {
					val txt = FileOperation.readTxtFile(new File(data.subFileName)).toList
					val temp = txt.map(_.split(",")).map { x =>
						val t = new IntegratedData()
						t.setHospNum(StringOption.takeStringSpace(x(0)).toInt)
						t.setHospName(StringOption.takeStringSpace(x(1)))
						t.setYearAndmonth(StringOption.takeStringSpace(x(2)).toInt)
						t.setMinimumUnit(StringOption.takeStringSpace(x(3)))
						t.setMinimumUnitCh(StringOption.takeStringSpace(x(4)))
						t.setMinimumUnitEn(StringOption.takeStringSpace(x(5)))
						t.setPhaid(StringOption.takeStringSpace(x(6)))
						t.setStrength(StringOption.takeStringSpace(x(7)))
						t.setMarket1Ch(StringOption.takeStringSpace(x(8)))
						t.setMarket1En(StringOption.takeStringSpace(x(9)))
						t.setSumValue(StringOption.takeStringSpace(x(10)).toDouble)
						t.setVolumeUnit(StringOption.takeStringSpace(x(11)).toDouble)
						t
					}
					router ! integratedresultext(temp)
				}
			}
			bus.publish(SplitEventBus.excelEnded(map))
			stay
		}

		case Event(startReadExcel(_), _) => {
			sender ! new masterBusy()
			stay
		}

		case Event(SplitAggregator.aggregatefinalresult(mr), data) => {
			self ! processing_data(mr)
			goto(MsaterPrecessingData) using data
		}

		case Event(requestMasterAverage_sub(sum), data) => {
            val reception = context.system.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/splitreception")
			reception ! requestMasterAverage(data.fileName, data.subFileName, sum)
            stay
        }

        case Event(responseMasterAverage(f, average), data) => {
            if (f == data.fileName) {
                agg ! responseaverage(average)
            }
            stay
        }
	}

	when(MsaterPrecessingData) {
		case Event(processing_data(mr), data) => {
			val time = DateUtil.getIntegralStartTime(new Date()).getTime
			new Insert().maxResultInsert(mr)(new InserAdapter().apply(data.fileName, data.getcompany, time))
			//context.actorSelection("akka.tcp://calc@127.0.0.1:2551/user/splitreception") ! freeMaster()
			goto(MsaterIdleing) using data.copy(fileName = "", getcompany = "", subFileName = "")
		}
	}

	whenUnhandled {
		case Event(e, s) => {
			println(s"cannot handle message $e  state is ${this.stateName}")
			stay
		}
	}

	import SplitMaster._
	import JobCategories._

	val bus = CreateSplitEventBus
	val agg = CreateSplitAggregator(bus)
	val router = CreateSplitWorker(agg)
}

trait CreateSplitWorker {
	this: Actor =>
	def CreateSplitWorker(a: ActorRef) = {
		context.actorOf(RoundRobinPool(10).props(SplitWorker.props(a)), name = "worker-router")
	}
}

trait CreateSplitEventBus {
	this: Actor =>
	def CreateSplitEventBus = new SplitEventBus(SplitMaster.num_count)
}

trait CreateSplitAggregator {
	this: Actor =>
	def CreateSplitAggregator(b: SplitEventBus) = {
		val a = context.actorOf(SplitAggregator.props(b, self))
		context.watch(a)
		a
	}
}