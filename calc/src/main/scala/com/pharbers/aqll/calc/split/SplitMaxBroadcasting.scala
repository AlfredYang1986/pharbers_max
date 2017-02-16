package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.actor.ActorRef
import com.pharbers.aqll.calc.excel.model.modelRunData
import scala.concurrent.stm._
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.excel.model.integratedData
import scala.collection.Iterator
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.excel.model.westMedicineIncome
import com.pharbers.aqll.calc.excel.model.ModelRunFactory
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData

object SplitMaxBroadcasting {
	def props(bus : SplitEventBus, hash : ActorRef) = Props(new SplitMaxBroadcasting(bus, hash))

	case class startmapping(n: Int) // broadcast to all hash actor
	case class premapping(m : ((Integer, String), IntegratedData))	// send group mapping from aggregator
//	case class premapping(m : List[IntegratedData])	// send group mapping from aggregator
	case class mappingiterator(d : modelRunData) extends broadcastingDefines	// send to hash mapping actor
	case class mappingiteratornext() // can perform next round of iterator
	case class mappingeof() extends broadcastingDefines
	case class mappingiteratorhashed(mrd : modelRunData)
}

class SplitMaxBroadcasting(bus : SplitEventBus, hash : ActorRef) extends Actor with ActorLogging {
	
	val group = ArrayBuffer[((Integer, String), IntegratedData)]()
	val group2 = ArrayBuffer[List[IntegratedData]]()
	var s : Stream[modelRunData] = Stream.Empty

	def receive = {
		case SplitMaxBroadcasting.premapping(m) => {
//      group2 += m

			group.find (p => p._1._1 == m._1._1 && p._1._2 == m._1._2 ) match {
				case Some(x) => Unit
				case None => group += m
			}
		}
		
		case SplitMaxBroadcasting.startmapping(n) => {
//      val tmp = group2.flatten.filterNot(_.getSegment == null).groupBy (x => (x.getYearAndmonth, x.getSegment)).map { x =>
//        (x._1, x._2.toList)
//      }
//
//      println(s"group2 list size: ${group2.flatten.size}")
//
//      println(s"group list size: ${tmp.size}")

      println(s"group list size: ${group.size}")

			s = new ModelRunFactory().apply(n, group.toStream)
	        nextiterator
		}
		case SplitMaxBroadcasting.mappingiteratornext() => nextiterator
		
		case x : AnyRef => println(s"message $x"); ???
	}
	
	def nextiterator = {
		if (!s.isEmpty) {
			hash ! SplitMaxBroadcasting.mappingiteratorhashed(s.head)
			s = s.tail
		} else {
			bus.publish(SplitMaxBroadcasting.mappingeof())
		}
	}
}