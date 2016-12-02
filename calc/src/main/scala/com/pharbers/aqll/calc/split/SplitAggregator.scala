package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.event.EventBus
import akka.actor.ActorLogging
import akka.actor.Props
import akka.event.ActorEventBus
import akka.event.LookupClassification
import scala.concurrent.stm._
import akka.actor.ActorRef

object SplitAggregator {
    def props(msgSize: Int, bus : SplitEventBus, master : ActorRef) = Props(new SplitAggregator(msgSize, bus, master))
    
    case class aggregatefinalresult(value : Double, unit : Double)
}

class SplitAggregator(msgSize: Int, bus : SplitEventBus, master : ActorRef) extends Actor {
	
	/**
	 * 所有的东西需要
	 */
	val avgsize = Ref(0)
	val rltsize = Ref(0)
	
	val unionSum = Ref(Stream(("",(0.0,0.0,0.0))))
	
	val value = Ref(0.0)
	val unit = Ref(0.0)
	
	import SplitWorker.requestaverage
	import SplitWorker.postresult
    def receive = {
		case requestaverage(sum) => {
			atomic { implicit thx => 
				avgsize() = avgsize() + 1
				unionSum() = unionSum() ++: sum
			}
			
			if (avgsize.single.get == msgSize) {
			    val sumAll = unionSum.single.get.tail.groupBy(_._1) map { x => 
			        (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
//			        (x._1, x._2.map(z => z._2._1).toList)
			    }
			    
				lazy val mapAvg = sumAll map { x =>
        	        (x._1,(x._2._1 / x._2._3),(x._2._2 / x._2._3))
        	    }
//				println(mapAvg.toList.mkString("\n"))
        	    bus.publish(SplitEventBus.average(mapAvg.toStream))
			}
			
		}
		case postresult(v, u) => {
			atomic { implicit thx => 
				rltsize() = rltsize() + 1
				value() = value() + v
				unit() = unit() + u
			}
			
			if (rltsize.single.get == msgSize) {
				println(s"value: ${value.single.get}")
				println(s"unit: ${unit.single.get}")
				
				master ! SplitAggregator.aggregatefinalresult(value.single.get, unit.single.get)
			}
		}
        case _ => println("aggregator")
    }
}