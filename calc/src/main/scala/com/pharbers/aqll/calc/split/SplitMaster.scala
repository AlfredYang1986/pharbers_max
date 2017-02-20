package com.pharbers.aqll.calc.split

import com.pharbers.aqll.calc.util.DateUtil
import com.pharbers.aqll.calc.common.DefaultData.{capLoadXmlPath, integratedXmlPath, phaLoadXmlPath}
import com.pharbers.aqll.calc.excel.core._
import com.pharbers.aqll.calc.maxmessages.{ cancel, end, startReadExcel, canHandling, masterBusy }
import com.pharbers.aqll.calc.excel.model.modelRunData

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import akka.cluster.routing.ClusterRouterPool
import akka.cluster.routing.ClusterRouterPoolSettings
import akka.routing.ConsistentHashingRouter._
import akka.routing.ConsistentHashingPool
import akka.routing.RoundRobinPool

import com.pharbers.aqll.calc.maxresult.Insert
import com.pharbers.aqll.calc.maxresult.InserAdapter

import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global

object SplitMaster {
	def props = Props[SplitMaster]
	val num_count = 10 // magic number
}

sealed trait MsaterState
case object MsaterIdleing extends MsaterState               // 空跑，空闲状态
case object MsaterPreAggCalcing extends MsaterState         // 数据检查和整合
case object MsaterWaitAggreating extends MsaterState        // 数据在内存中等待
case object MsaterPostAggCalcing extends MsaterState        // 数据计算
case object MsaterPrecessingData extends MsaterState        // 数据计算结果写入数据库中

case object MsaterCalcing extends MsaterState               // 计算中，以后细化

case class MsaterStateData(var fileName : String, var getcompany : String)

class SplitMaster extends Actor with ActorLogging
	with CreateSplitWorker 
	with CreateSplitEventBus
	with CreateSplitAggregator
    with FSM[MsaterState, MsaterStateData] {

    startWith(MsaterIdleing, new MsaterStateData("", ""))

    when(MsaterIdleing) {
        case Event(startReadExcel(map), data) => {
            data.getcompany = map.get("company").get.toString
            data.fileName = map.get("filename").get.toString

            goto(MsaterPreAggCalcing) using data
            sender ! canHandling()

            (map.get("JobDefines").get.asInstanceOf[JobDefines].t match {
                case 0 => {
                    row_cpamarketinteractparser(capLoadXmlPath.cpamarketxmlpath_en,
                        capLoadXmlPath.cpamarketxmlpath_ch,
                        router)
                }
                case 1 => {
                    row_cpaproductinteractparser(capLoadXmlPath.cpaproductxmlpath_en,
                        capLoadXmlPath.cpaproductxmlpath_ch,
                        router)
                }
                case 2 => {
                    row_phamarketinteractparser(phaLoadXmlPath.phamarketxmlpath_en,
                        phaLoadXmlPath.phamarketxmlpath_ch,
                        router)
                }
                case 3 => {
                    row_phaproductinteractparser(phaLoadXmlPath.phaproductxmlpath_en,
                        phaLoadXmlPath.phaproductxmlpath_ch,
                        router)
                }
                case _ => {
                    row_integrateddataparser(integratedXmlPath.integratedxmlpath_en,
                        integratedXmlPath.integratedxmlpath_ch,
                        router)
                }
            }).startParse(map.get("filename").get.toString, 1)
            bus.publish(SplitEventBus.excelEnded(map))
        }
    }

    when(MsaterCalcing) {
        case Event(startReadExcel(_), _) =>{
            sender ! masterBusy()
            stay
        }

        case Event(SplitAggregator.aggregatefinalresult(mr), data) => {
            goto(MsaterPrecessingData) using data

            val time = DateUtil.getIntegralStartTime(new Date()).getTime
            new Insert().maxResultInsert(mr)(new InserAdapter().apply(data.fileName, data.getcompany, time))
            context.stop(self)
            System.gc()
        }
    }

    whenUnhandled {
        case Event(e, s) => {
            println(s"cannot handle message $e")
            stay
        }
    }

	import SplitMaster._
	import JobCategories._
	
	val bus = CreateSplitEventBus
	val agg = CreateSplitAggregator(bus)
	val router = CreateSplitWorker(agg)
}

trait CreateSplitWorker { this : Actor =>
	def CreateSplitWorker(a : ActorRef) = {
//	    context.actorOf(
//            ClusterRouterPool(RoundRobinPool(10), ClusterRouterPoolSettings(    
//                totalInstances = 10, maxInstancesPerNode = SplitMaster.num_count,
//                allowLocalRoutees = true, useRole = None)).props(SplitWorker.props(a)),
//              name = "worker-router")
		context.actorOf(RoundRobinPool(10).props(SplitWorker.props(a)), name = "worker-router")
	}
}

trait CreateSplitEventBus { this : Actor => 
	def CreateSplitEventBus = new SplitEventBus(SplitMaster.num_count)
}

trait CreateSplitAggregator { this : Actor => 
    def CreateSplitAggregator(b : SplitEventBus) = {
    	val a = context.actorOf(SplitAggregator.props(b, self))
    	context.watch(a)
    	a
    }
}