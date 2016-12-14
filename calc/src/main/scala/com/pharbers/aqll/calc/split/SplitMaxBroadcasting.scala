package com.pharbers.aqll.calc.split

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import com.pharbers.aqll.calc.excel.model.modelRunData
import scala.concurrent.stm._
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.excel.model.integratedData

object SplitMaxBroadcasting {
	def props(bus : SplitEventBus) = Props(new SplitMaxBroadcasting(bus))

	case class startmapping() // broadcast to all hash actor
	case class premapping(m : ((Int, Int, String), integratedData))	// send group mapping from aggregator
	case class mappingiterator(d : modelRunData) extends broadcastingDefines	// send to hash mapping actor
	case class mappingiteratornext() // can perform next round of iterator
	case class mappingeof() extends broadcastingDefines
}

class SplitMaxBroadcasting(bus : SplitEventBus) extends Actor with ActorLogging {
	
	val group = Ref(List[((Int, Int, String), integratedData)]())
	var s : Stream[modelRunData] = Stream.Empty
	
	def receive = {
		case SplitMaxBroadcasting.premapping(m) => {
			atomic { implicit thx => 
				group() = group().filterNot(p => p._1._1 == m._1._1 && p._1._2 == m._1._2 && p._1._3 == m._1._3) :+ m 
			}
		}
		
		case SplitMaxBroadcasting.startmapping() => {
			println(s"group list size: ${group.single.get.size}")
			
			val output = group.single.get.toStream
	        s = (output.map { x =>
	        	val element2 = x._2
	            DefaultData.hospdatabase map { element =>
	                 new modelRunData(element.getCompany, element2.uploadYear, element2.uploadMonth, 0.0, 0.0, element2.minimumUnit, element2.minimumUnitCh, element2.minimumUnitEn, element2.manufacturerCh, element2.manufacturerEn, element2.generalnameCh, element2.generalnameEn, element2.tradenameCh, element2.tradenameEn, element2.dosageformsCh, element2.dosageformsEn, element2.drugspecificationsCh, element2.drugspecificationsEn, element2.numberpackagingCh, element2.numberpackagingEn, element2.skuCh, element2.skuEn, element2.market1Ch, element2.market1En, element.getSegment, element.getFactor, element.getIfPanelAll, element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid, element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince, element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2, element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum, element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum, element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum, element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome, element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome, element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome, element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome, element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
	             }
	        }).flatten
	        nextiterator
		}
		case SplitMaxBroadcasting.mappingiteratornext() => nextiterator
		
		case x : AnyRef => println(s"message $x"); ???
	}
	
	def nextiterator = {
		if (!s.isEmpty) {	
			val head = s.head
			bus.publish(SplitMaxBroadcasting.mappingiterator(head))
			s = s.tail
		} else {
			bus.publish(SplitMaxBroadcasting.mappingeof())
		}
	}
}