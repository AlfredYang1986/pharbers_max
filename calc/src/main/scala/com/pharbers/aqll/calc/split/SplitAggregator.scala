package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.event.EventBus
import akka.actor.ActorLogging
import akka.actor.Props
import akka.event.ActorEventBus
import akka.event.LookupClassification
import scala.concurrent.stm._
import akka.actor.ActorRef
import com.pharbers.aqll.calc.excel.model.modelRunData
import java.util.Date
import com.pharbers.aqll.calc.util.DateUtil

object SplitAggregator {
    def props(msgSize: Int, bus : SplitEventBus, master : ActorRef) = Props(new SplitAggregator(msgSize, bus, master))
    
    case class aggregatefinalresult(mr: Stream[(Long, (Double, Double))])
}

class SplitAggregator(msgSize: Int, bus : SplitEventBus, master : ActorRef) extends Actor {
	
	/**
	 * 所有的东西需要
	 */
	val avgsize = Ref(0)
	val rltsize = Ref(0)
	
	val unionSum = Ref(Stream[(String, (Double, Double, Double))]())
	
	val mrResult = Ref(Stream[modelRunData]())
	
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
				
				mrResult() = mrResult() ++: mr
			}
			
			if (rltsize.single.get == msgSize) {
				val result = mrResult.single.get.groupBy ( x => (x.uploadYear,x.uploadMonth) ) map { x =>
				    (DateUtil.getDateLong(x._1._1,x._1._2),(x._2 map(_.finalResultsValue) sum, x._2 map(_.finalResultsUnit) sum))
				}
				master ! SplitAggregator.aggregatefinalresult(result.toStream)
			}
		}
        case _ => println("aggregator")
    }
}