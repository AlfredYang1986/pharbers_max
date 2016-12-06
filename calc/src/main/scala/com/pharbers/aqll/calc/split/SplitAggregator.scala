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

object SplitAggregator {
    def props(msgSize: Int, bus : SplitEventBus, master : ActorRef) = Props(new SplitAggregator(msgSize, bus, master))
    
    case class aggregatefinalresult(mr: List[(Long, (Double, Double))])
    case class aggsubcribe(a : ActorRef)
}

class SplitAggregator(msgSize: Int, bus : SplitEventBus, master : ActorRef) extends Actor {
	
	val avgsize = Ref(0)
	val rltsize = Ref(0)
	val mapsize = Ref(0)
	
	val unionSum = Ref(List[(String, (Double, Double, Double))]())
	val mrResult = Ref(Map[Long, (Double, Double)]())
	
	import SplitWorker.requestaverage
	import SplitWorker.postresult
    def receive = {
		case requestaverage(sum) => {
			atomic { implicit thx => 
				avgsize() = avgsize() + 1
				unionSum() = unionSum() ++: sum
			}
			
			if (avgsize.single.get == msgSize) {
			    val sumAll = unionSum.single.get.groupBy(_._1) map { x => 
			        (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
			    }
			    
				lazy val mapAvg = sumAll map { x =>
        	        (x._1,(x._2._1 / x._2._3),(x._2._2 / x._2._3))
        	    }
        	    bus.publish(SplitEventBus.average(mapAvg.toStream))
			}
			
		}
		case postresult(mr) => {
			atomic { implicit thx => 
				rltsize() = rltsize() + 1
				println(s"rltsize.single.get = ${rltsize.single.get}")
				mr.foreach { kvs => 
					val (v, u) = mrResult().get(kvs._1).map { x => 
						(x._1 + kvs._2._1, x._2 + kvs._2._2)
					}.getOrElse(kvs._2._1, kvs._2._2)
					mrResult() = mrResult() + (kvs._1 -> (v, u))
				}
			}
			
			if (rltsize.single.get == msgSize) {
				val result = mrResult.single.get
				val check = result.map (x => x._2._1).sum
				println(s"final result is : $check")
//				master ! SplitAggregator.aggregatefinalresult(result)
			}
		}
		case SplitAggregator.aggsubcribe(a) => {
			bus.subscribe(a, "AggregorBus")
		}
		case SplitHashMappingWorker.HashMappingEnd() => {
			atomic { implicit thx => 
				mapsize() = mapsize() + 1
			}
			
			if (mapsize.single.get == msgSize) {
        	    bus.publish(SplitHashMappingWorker.HashMappingEnd())
			}
		}
        case x : AnyRef => println(x); ???
    }
}