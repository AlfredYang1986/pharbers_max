package com.pharbers.aqll.calc.split

import scala.Stream
import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic

import com.pharbers.aqll.calc.excel.model.modelRunData
import com.pharbers.aqll.calc.util.DateUtil

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.agent.Agent
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.excel.model.integratedData
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.typesafe.config.ConfigFactory
import akka.cluster.routing.ClusterRouterPool
import akka.cluster.routing.ClusterRouterPoolSettings
import akka.routing.ConsistentHashingPool
import com.pharbers.aqll.calc.datacala.common._
import com.pharbers.aqll.calc.excel.CPA._
import com.pharbers.aqll.calc.excel.PharmaTrust._

object SplitAggregator {
    def props(bus : SplitEventBus, master : ActorRef) = Props(new SplitAggregator(bus, master))

    case class aggregatefinalresult(mr: List[(String, (Long, Double, Double, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], String, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)]))])
    case class excelResult(exd: (Double, Double, Int, List[(String)], List[(String)]))
    case class aggsubcribe(a : ActorRef)
    case class aggmapsubscrbe(a : ActorRef)
    
    case class integratedata(data : List[integratedData])
    
    val mapping_nr_of_instance_in_node = 10
    val mapping_nr_of_node = 1
    val mapping_nr_total_instance = mapping_nr_of_instance_in_node * mapping_nr_of_node
    case class msg_container(group : (Integer, String), lst : List[IntegratedData])
}

class SplitAggregator(bus : SplitEventBus, master : ActorRef) extends Actor with CreateMappingActor with CreateMaxBroadcastingActor {
	
	val unionSum = Ref(List[(String, (Double, Double, Double))]())
	val mrResult = Ref(Map[String, (Long, Double, Double, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], String, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)])]())
	val excelcheckdata = Ref(List[(Double, Double, Long, String)]())
	
	val mapping_master_router = CreateMappingActor
	val excelsize = Ref(0)
	val excelshouleszie = Ref(0)
	val excelchecksize = Ref(0)
	
	val avgsize = Ref(0)
	val rltsize = Ref(0)
	val mapshouldsize = Ref(0)
	
	val broadcasting_actor = CreateMaxBroadcastingActor(bus, mapping_master_router)
	
	import SplitWorker.requestaverage
	import SplitWorker.postresult
    def receive = {
		case requestaverage(sum) => {
			atomic { implicit thx => 
				avgsize() = avgsize() + 1
				unionSum() = unionSum() ++: sum
			}

			println(s"average ${avgsize.single.get} whith sender $sender")
			if (avgsize.single.get == mapshouldsize.single.get) {
			    val sumAll = unionSum.single.get.groupBy(_._1) map { x =>
			        (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
			    }
			    
				lazy val mapAvg = sumAll map { x =>
        	        (x._1, (x._2._1 / x._2._3),(x._2._2 / x._2._3))
        	    }
                println(s"mapAvg = ${mapAvg}")
        	    bus.publish(SplitEventBus.average(mapAvg.toList))
			}
			
		}
		
		case postresult(mr) => {
			atomic { implicit thx =>
				println(s"postresult = ${mr.size}")
				rltsize() = rltsize() + 1
				mr.foreach { kvs =>
					val (t, v, u, h, p, m, s, city, toall, touse, segment) = mrResult().get(kvs._1).map { x =>
						kvs._2._4.foreach(x._4.distinct.append(_))
						kvs._2._5 foreach(x._5.distinct.append(_))
						kvs._2._6.foreach(x._6.distinct.append(_))
					    (x._1, x._2 + kvs._2._2, x._3 + kvs._2._3, x._4 , x._5, x._6, kvs._2._7, kvs._2._8, kvs._2._9, kvs._2._10, kvs._2._11)
					}.getOrElse(kvs._2._1, kvs._2._2, kvs._2._3, kvs._2._4, kvs._2._5, kvs._2._6, kvs._2._7, kvs._2._8, kvs._2._9, kvs._2._10, kvs._2._11)
					mrResult() = mrResult() + (kvs._1 -> (t, v, u, h, p, m, s, city, toall, touse, segment))
				}
			}
			
			if (rltsize.single.get == mapshouldsize.single.get) {
				val result = mrResult.single.get
                println(s"result = ${result.size}")
				master ! SplitAggregator.aggregatefinalresult(result.toList)
			}
		}
		case SplitAggregator.aggsubcribe(a) => {
			atomic { implicit thx => 
				excelshouleszie() = excelshouleszie() + 1
			}
			println(s"worker should size ${excelshouleszie.single.get}")
			bus.subscribe(a, "AggregorBus")
		}
		case SplitAggregator.aggmapsubscrbe(a) => {
			atomic { implicit thx => 
				mapshouldsize() = mapshouldsize() + 1
			}
			
			println(s"map should size ${mapshouldsize.single.get}")
			bus.subscribe(a, "AggregorBus")
		}
			
		case SplitWorker.integratedataended(n) => {
			atomic { implicit thx => 
				excelsize() = excelsize() + 1
			}
	
			println(s"integratedata ended ${excelsize.single.get}")
			if (excelsize.single.get == excelshouleszie.single.get) {
				broadcasting_actor ! SplitMaxBroadcasting.startmapping(n)
			}
		}
		case SplitWorker.integratedataresult(m) =>
	        m map { tmp =>
	            broadcasting_actor ! SplitMaxBroadcasting.premapping((tmp._1, tmp._2.head))
	            mapping_master_router ! SplitAggregator.msg_container(tmp._1, tmp._2)
			}
		case SplitMaxBroadcasting.mappingiteratornext() => {
			broadcasting_actor ! SplitMaxBroadcasting.mappingiteratornext()
		}

        case x : AnyRef => println(x); ???
    }
}

import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.excel.CPA.CpaProduct
import com.pharbers.aqll.calc.excel.CPA.CpaProduct
import com.pharbers.aqll.calc.excel.CPA.CpaProduct
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.common.DefaultData

trait CreateMappingActor { this : Actor =>
	
	def AggregateHashMapping : ConsistentHashMapping = {
		case SplitAggregator.msg_container(group ,lst) => group._1 + group._2
		case SplitMaxBroadcasting.mappingiteratorhashed(mrd) => mrd.yearAndmonth + mrd.minimumUnitCh
	} 

	def CreateMappingActor = {
//		context.actorOf(
//			ClusterRouterPool(ConsistentHashingPool(SplitAggregator.mapping_nr_total_instance, hashMapping = AggregateHashMapping), ClusterRouterPoolSettings(    
//            	totalInstances = SplitAggregator.mapping_nr_total_instance, maxInstancesPerNode = SplitAggregator.mapping_nr_of_instance_in_node,
//                allowLocalRoutees = true, useRole = None)).props(SplitGroupMaster.props(self)), name = "mapping-route")
		context.actorOf(ConsistentHashingPool(SplitAggregator.mapping_nr_total_instance, hashMapping = AggregateHashMapping).props(SplitGroupMaster.props(self)), name = "mapping-route")
	}
}

trait CreateMaxBroadcastingActor { this : Actor => 
	def CreateMaxBroadcastingActor(b : SplitEventBus, h : ActorRef) = context.actorOf(SplitMaxBroadcasting.props(b, h))
}