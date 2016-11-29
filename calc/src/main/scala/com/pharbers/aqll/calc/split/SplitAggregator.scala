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
	
	val sum_1 = Ref(0.0)
	val sum_2 = Ref(0.0)
	val sum_3 = Ref(0.0)
	
	val value = Ref(0.0)
	val unit = Ref(0.0)
	
	import SplitWorker.requestaverage
	import SplitWorker.postresult
    def receive = {
		case requestaverage(sum1, sum2, sum3) => {
			atomic { implicit thx => 
				avgsize() = avgsize() + 1
				sum_1() = sum_1() + sum1
				sum_2() = sum_2() + sum2
				sum_3() = sum_3() + sum3
			}
			
			if (avgsize.single.get == msgSize) {
				println(s"sum_1 : ${sum_1.single.get}")
				println(s"sum_2 : ${sum_2.single.get}")
				println(s"sum_3 : ${sum_3.single.get}")
				bus.publish(SplitEventBus.average(sum_1.single.get / sum_2.single.get, sum_2.single.get / sum_3.single.get))
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