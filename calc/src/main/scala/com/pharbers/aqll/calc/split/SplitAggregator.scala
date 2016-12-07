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

object SplitAggregator {
    def props(msgSize: Int, bus : SplitEventBus, master : ActorRef) = Props(new SplitAggregator(msgSize, bus, master))
    
    case class aggregatefinalresult(mr: List[(Long, (Double, Double))])
    case class aggsubcribe(a : ActorRef)
    
    case class integratedata(data : List[integratedData])
}

class SplitAggregator(msgSize: Int, bus : SplitEventBus, master : ActorRef) extends Actor {
	
	val avgsize = Ref(0)
	val rltsize = Ref(0)
	
	val unionSum = Ref(List[(String, (Double, Double, Double))]())
	val mrResult = Ref(Map[Long, (Double, Double)]())
	
	val mapping_master_actor = Ref(Map[(Int, Int, String), ActorRef]())
	val mapsize = Ref(0)
	
	import SplitWorker.requestaverage
	import SplitWorker.postresult
    def receive = {
		case requestaverage(sum) => {
			atomic { implicit thx => 
				avgsize() = avgsize() + 1
				unionSum() = unionSum() ++: sum
			}
			
			if (avgsize.single.get == mapping_master_actor.single.get.size) {
			    val sumAll = unionSum.single.get.groupBy(_._1) map { x => 
			        (x._1, (x._2.map(z => z._2._1).sum, x._2.map(z => z._2._2).sum, x._2.map(z => z._2._3).sum))
			    }
			    
				lazy val mapAvg = sumAll map { x =>
        	        (x._1,(x._2._1 / x._2._3),(x._2._2 / x._2._3))
        	    }
//        	    bus.publish(SplitEventBus.average(mapAvg.toStream))
        	    mapping_master_actor.single.get foreach { kva => 
					kva._2 ! SplitEventBus.average(mapAvg.toList)
				}
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
			
			if (rltsize.single.get == mapping_master_actor.single.get.size) {
				val result = mrResult.single.get
				val check = result.map (x => x._2._1).sum
				println(s"final result is : $check")
//				master ! SplitAggregator.aggregatefinalresult(result)
			}
		}
		case SplitAggregator.aggsubcribe(a) => {
			bus.subscribe(a, "AggregorBus")
		}
		case SplitWorker.integratedataresult(m) => {
			atomic { implicit thx => 
				mapsize() = mapsize() + 1
			}
			
			m.map { kvs => 
				mapping_master_actor.single.get.get(kvs._1) match {
					case Some(a) => a ! SplitGroupMaster.groupintegrated(kvs._2)
					case None => {
						val a = context.system.actorOf(SplitGroupMaster.props(self))
						atomic { implicit thx =>
							mapping_master_actor() = mapping_master_actor() + (kvs._1 -> a)
						}
						a ! SplitGroupMaster.groupintegrated(kvs._2)
					}
				}
			}
		
			if (mapsize.single.get == msgSize) {
				println("publish to mapping")
        	    mapping_master_actor.single.get foreach { kva => 
					kva._2 ! SplitGroupMaster.mappingend()
				}
			}
		}
        case x : AnyRef => println(x); ???
    }
}