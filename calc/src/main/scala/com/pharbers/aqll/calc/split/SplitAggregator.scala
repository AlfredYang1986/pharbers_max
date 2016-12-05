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

object SplitAggregator {
    def props(msgSize: Int, bus : SplitEventBus, master : ActorRef) = Props(new SplitAggregator(msgSize, bus, master))
    
    case class aggregatefinalresult(mr: Stream[(Long, (Double, Double))])
    case class aggsubcribe(a : ActorRef)
}

class SplitAggregator(msgSize: Int, bus : SplitEventBus, master : ActorRef) extends Actor {
	
	val avgsize = Ref(0)
	val rltsize = Ref(0)
	
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
				mr.foreach { kvs => 
					val (v, u) = mrResult().get(kvs._1).map { x => 
						(x._1 + kvs._2._1, x._2 + kvs._2._2)
					}.getOrElse(kvs._2._1, kvs._2._2)
					mrResult() = mrResult() + (kvs._1 -> (v, u))
				}
			}
			
			if (rltsize.single.get == msgSize) {
				val result = mrResult.single.get
				master ! SplitAggregator.aggregatefinalresult(result.toStream)
			}
		}
		case SplitAggregator.aggsubcribe(a) => {
			bus.subscribe(a, "AggregorBus")
		}
        case _ => ???
    }
}