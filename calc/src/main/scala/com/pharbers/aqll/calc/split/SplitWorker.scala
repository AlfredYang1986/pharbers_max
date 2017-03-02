package com.pharbers.aqll.calc.split

import scala.concurrent.stm._
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.datacala.common._
import com.pharbers.aqll.calc.excel.core._
import com.pharbers.aqll.calc.excel.model.integratedData
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.excel.model.modelRunData
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.EventBus

import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.datacala.algorithm.maxSumData
import com.pharbers.aqll.calc.datacala.algorithm.maxCalcData
import com.pharbers.aqll.calc.util.{DateUtil, MD5}
import com.pharbers.aqll.calc.datacala.module.MaxMessage.msg_IntegratedData
import com.pharbers.aqll.calc.datacala.module.MaxModule
import com.pharbers.aqll.calc.adapter.SplitAdapter
import com.pharbers.aqll.calc.excel.CPA.CpaProduct
import com.pharbers.aqll.calc.excel.CPA.CpaMarket
import com.pharbers.aqll.calc.excel.PharmaTrust.PharmaTrustPorduct
import com.pharbers.aqll.calc.excel.PharmaTrust.PharmaTrustMarket
import com.pharbers.aqll.calc.split.SplitWorker.integratedresultext

object SplitWorker {
	def props(a: ActorRef) = Props(new SplitWorker(a))

	case class requestaverage(sum: List[(String, (Double, Double, Double))])
	case class responseaverage(sum: List[(String, Double, Double)])

	case class postresult(mr: Map[String, (Long, Double, Double, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], String, ArrayBuffer[String], ArrayBuffer[String], ArrayBuffer[String], ArrayBuffer[String])])

	case class integratedataresult(integrated: Map[(Integer, String), List[IntegratedData]])

	//	case class integratedataended(n: Int)
	case class integratedataended(map: Map[String, Any])

	case class exceluniondata(e: List[(Double, Double, Long, String)])

	case class integratedresultext(d: IntegratedData)

}

//object adminData {
//    lazy val hospbasedata = DefaultData.hospdatabase
//	lazy val hospmatchdata = DefaultData.hospmatchdata
//	lazy val market = DefaultData.marketdata
//	lazy val product = DefaultData.productdata
//}

class SplitWorker(aggregator: ActorRef) extends Actor with ActorLogging with CreateSplitWorker {
	val data: ArrayBuffer[integratedData] = ArrayBuffer.empty
	val data2: ArrayBuffer[IntegratedData] = ArrayBuffer.empty

	val excelunion: ArrayBuffer[(Double, Double, Long, String)] = ArrayBuffer.empty
	val subFun = aggregator ! SplitAggregator.aggsubcribe(self)

	val cpaproexcel: ArrayBuffer[CpaProduct] = ArrayBuffer.empty
	val cpomarexcel: ArrayBuffer[CpaMarket] = ArrayBuffer.empty
	val phaproexcel: ArrayBuffer[PharmaTrustPorduct] = ArrayBuffer.empty
	val phamarexcel: ArrayBuffer[PharmaTrustMarket] = ArrayBuffer.empty

	val idle: Receive = {
		case integratedresult(target) => {
			data2 ++= (target :: Nil)
		}
		case integratedresultext(target) => {
			data2 ++= (target :: Nil)
		}
		case SplitEventBus.excelEnded(map) => {
			println(s"read ended at $self")
			val year = data2.map(_.getYearAndmonth).distinct.head.toString.substring(0, 4)
			val market = data2.map(_.getMarket1Ch).distinct.head
			//"HospitalData/20000家pfizer医院数据库表.xlsx"
			val m = map.updated("hospdatapath",MD5.md5(map.get("company").get.toString+year+market))
			val tmp = data2.toList.groupBy(x => (x.getYearAndmonth, x.getMinimumUnitCh))
			aggregator ! SplitWorker.integratedataresult(tmp)

			aggregator ! SplitWorker.integratedataended(m)
		}
		case _ => Unit
	}

	val working: Receive = {
		case cpamarketresult(target) => {}

		case _ => ???
	}

	def receive = idle

	def cancelActor = {
		context.stop(self)
	}
}