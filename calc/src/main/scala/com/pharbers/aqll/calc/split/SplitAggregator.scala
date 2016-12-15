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
import scala.collection.mutable.Map
import com.pharbers.aqll.calc.excel.model.integratedData
import com.typesafe.config.ConfigFactory
import akka.cluster.routing.ClusterRouterPool
import akka.cluster.routing.ClusterRouterPoolSettings
import akka.routing.ConsistentHashingPool

object SplitAggregator {
    def props(bus : SplitEventBus, master : ActorRef) = Props(new SplitAggregator(bus, master))
    
    case class aggregatefinalresult(mr: List[(Long, (Double, Double))])
    case class aggsubcribe(a : ActorRef)
    case class aggmapsubscrbe(a : ActorRef)
    
    case class integratedata(data : List[integratedData])
    
    val mapping_nr_of_instance_in_node = 8
    val mapping_nr_of_node = 2
    val mapping_nr_total_instance = mapping_nr_of_instance_in_node * mapping_nr_of_node
    
    case class msg_container(group : (Int, Int, String), lst : List[integratedData])
}

class SplitAggregator(bus : SplitEventBus, master : ActorRef) extends Actor with CreateMappingActor with CreateMaxBroadcastingActor {
	
	val unionSum = Ref(List[(String, (Double, Double, Double))]())
	val mrResult = Ref(Map[Long, (Double, Double)]())
	
	val mapping_master_router = CreateMappingActor
	val excelsize = Ref(0)
	val excelshouleszie = Ref(0)
	
	val avgsize = Ref(0)
	val rltsize = Ref(0)
	val mapshouldsize = Ref(0)
	
	val broadcasting_actor = CreateMaxBroadcastingActor(bus, mapping_master_router)
//	val iterator_count = Ref(0)
	
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
        	    bus.publish(SplitEventBus.average(mapAvg.toList))
			}
			
		}
		case postresult(mr) => {
			atomic { implicit thx => 
				rltsize() = rltsize() + 1
				mr.foreach { kvs => 
					val (v, u) = mrResult().get(kvs._1).map { x => 
						(x._1 + kvs._2._1, x._2 + kvs._2._2)
					}.getOrElse(kvs._2._1, kvs._2._2)
					mrResult() = mrResult() + (kvs._1 -> (v, u))
				}
			}
			
			if (rltsize.single.get == mapshouldsize.single.get) {
				val result = mrResult.single.get
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
			
		case SplitWorker.integratedataended() => {
			atomic { implicit thx => 
				excelsize() = excelsize() + 1
			}
	
			println(s"integratedata ended ${excelsize.single.get}")
//			if (excelsize.single.get == msgSize) {
			if (excelsize.single.get == excelshouleszie.single.get) {
//				bus.publish(SplitGroupMaster.mappingend())
				broadcasting_actor ! SplitMaxBroadcasting.startmapping()
			}
		}
		case SplitWorker.integratedataresult(m) => {
			broadcasting_actor ! SplitMaxBroadcasting.premapping((m._1, m._2.head))
			mapping_master_router ! SplitAggregator.msg_container(m._1, m._2)
		}
		case SplitMaxBroadcasting.mappingiteratornext() => {
//			atomic { implicit thx => 
//				iterator_count() = iterator_count() + 1
//			}
//			
//			if (iterator_count.single.get == mapshouldsize.single.get) {
//				atomic { implicit thx => 
//					iterator_count() = 0
//				}
//			
				broadcasting_actor ! SplitMaxBroadcasting.mappingiteratornext()
//			}
		}
        case x : AnyRef => println(x); ???
    }
}

import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
trait CreateMappingActor { this : Actor =>
	
	def AggregateHashMapping : ConsistentHashMapping = {
		case SplitAggregator.msg_container(group, lst) => group._1 + group._2 + group._3
		case SplitMaxBroadcasting.mappingiteratorhashed(mrd) => mrd.getUploadYear + mrd.getUploadMonth + mrd.getMinimumUnitCh 
	} 
	
	def CreateMappingActor = {
		context.actorOf(
			ClusterRouterPool(ConsistentHashingPool(SplitAggregator.mapping_nr_total_instance, hashMapping = AggregateHashMapping), ClusterRouterPoolSettings(    
            	totalInstances = SplitAggregator.mapping_nr_total_instance, maxInstancesPerNode = SplitAggregator.mapping_nr_of_instance_in_node,
                allowLocalRoutees = false, useRole = None)).props(SplitGroupMaster.props(self)), name = "mapping-route") 
	}
}

trait CreateMaxBroadcastingActor { this : Actor => 
	def CreateMaxBroadcastingActor(b : SplitEventBus, h : ActorRef) = context.actorOf(SplitMaxBroadcasting.props(b, h))
}