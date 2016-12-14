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
    def props(msgSize: Int, bus : SplitEventBus, master : ActorRef) = Props(new SplitAggregator(msgSize, bus, master))
    
    case class aggregatefinalresult(mr: List[(Long, (Double, Double))])
    case class aggsubcribe(a : ActorRef)
    case class aggmapsubscrbe(a : ActorRef)
    
    case class integratedata(data : List[integratedData])
    
    val mapping_nr_of_instance_in_node = 10
    val mapping_nr_of_node = 2
    val mapping_nr_total_instance = mapping_nr_of_instance_in_node * mapping_nr_of_node
    
    case class msg_container(group : (Int, Int, String), lst : List[integratedData])
}

class SplitAggregator(msgSize: Int, bus : SplitEventBus, master : ActorRef) extends Actor with CreateMappingActor {
	
	val avgsize = Ref(0)
	val rltsize = Ref(0)
	
	val unionSum = Ref(List[(String, (Double, Double, Double))]())
	val mrResult = Ref(Map[Long, (Double, Double)]())
	
	val mapping_master_router = CreateMappingActor
	val mapsize = Ref(0)
	val mapshouldsize = Ref(0)
	
	import SplitWorker.requestaverage
	import SplitWorker.postresult
    def receive = {
		case requestaverage(sum) => {
			atomic { implicit thx => 
				avgsize() = avgsize() + 1
				unionSum() = unionSum() ++: sum
			}

			println(s"average ${avgsize.single.get} whith sender $sender")
//			if (avgsize.single.get == SplitAggregator.mapping_nr_total_instance) {
			if (avgsize.single.get == mapshouldsize.single.get) {
			    val sumAll = unionSum.single.get.groupBy(_._1) map { x => 
			        (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
			    }
			    
				lazy val mapAvg = sumAll map { x =>
        	        (x._1,(x._2._1 / x._2._3),(x._2._2 / x._2._3))
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
			
//			if (rltsize.single.get == SplitAggregator.mapping_nr_total_instance) {
			if (avgsize.single.get == mapshouldsize.single.get) {
				val result = mrResult.single.get
				master ! SplitAggregator.aggregatefinalresult(result.toList)
			}
		}
		case SplitAggregator.aggsubcribe(a) => {
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
				mapsize() = mapsize() + 1
			}
	
			println(s"integratedata ended ${mapsize.single.get}")
			if (mapsize.single.get == msgSize) {
				bus.publish(SplitGroupMaster.mappingend())
			}
		}
		case SplitWorker.integratedataresult(m) => mapping_master_router ! SplitAggregator.msg_container(m._1, m._2)
        case x : AnyRef => println(x); ???
    }
}

import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
trait CreateMappingActor { this : Actor =>
	
	def AggregateHashMapping : ConsistentHashMapping = {
		case SplitAggregator.msg_container(group, lst) => group._1 + group._2 + group._3
	} 
	
	def CreateMappingActor = {
		context.actorOf(
			ClusterRouterPool(ConsistentHashingPool(SplitAggregator.mapping_nr_total_instance, hashMapping = AggregateHashMapping), ClusterRouterPoolSettings(    
            	totalInstances = SplitAggregator.mapping_nr_total_instance, maxInstancesPerNode = SplitAggregator.mapping_nr_of_instance_in_node,
                allowLocalRoutees = true, useRole = None)).props(SplitGroupMaster.props(self)), name = "mapping-route") 
	}
}