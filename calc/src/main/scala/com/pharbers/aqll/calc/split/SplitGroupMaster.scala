package com.pharbers.aqll.calc.split

import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import com.pharbers.aqll.calc.excel.model.integratedData
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.excel.model.integratedData
import com.pharbers.aqll.calc.excel.model.modelRunData
import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.datacala.algorithm.maxCalcData
import com.pharbers.aqll.calc.datacala.algorithm.maxSumData
import com.pharbers.aqll.calc.util.DateUtil
import com.pharbers.aqll.calc.datacala.common.BaseMaxDataArgs
import com.pharbers.aqll.calc.datacala.common.AdminHospDataBaseArgs
import com.pharbers.aqll.calc.datacala.common.IntegratedDataArgs
import com.pharbers.aqll.calc.datacala.module.MaxMessage.msg_MaxData
import com.pharbers.aqll.calc.datacala.module.MaxModule
import com.pharbers.aqll.calc.datacala.common.ModelRunDataArgs

object SplitGroupMaster {
	def props(a : ActorRef) = Props(new SplitGroupMaster(a))
	
	case class groupintegrated(lst : List[integratedData])
	case class mappingend()
}

class SplitGroupMaster(aggregator : ActorRef) extends Actor with ActorLogging {
	
  val r : ArrayBuffer[modelRunData] = ArrayBuffer.empty
	
	def receive = {
		case SplitGroupMaster.groupintegrated(lst) => {
			val output = lst.toList.groupBy(x => (x.getUploadYear, x.getUploadMonth, x.getMinimumUnitCh)).map(_._2.head).toList
	        (output map { element2 =>
	             DefaultData.hospdatabase.toList map { element =>
	                 val mrd = new modelRunData(element.getCompany, element2.uploadYear, element2.uploadMonth, 0.0, 0.0, element2.minimumUnit, element2.minimumUnitCh, element2.minimumUnitEn, element2.manufacturerCh, element2.manufacturerEn, element2.generalnameCh, element2.generalnameEn, element2.tradenameCh, element2.tradenameEn, element2.dosageformsCh, element2.dosageformsEn, element2.drugspecificationsCh, element2.drugspecificationsEn, element2.numberpackagingCh, element2.numberpackagingEn, element2.skuCh, element2.skuEn, element2.market1Ch, element2.market1En, element.getSegment, element.getFactor, element.getIfPanelAll, element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid, element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince, element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2, element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum, element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum, element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum, element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome, element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome, element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome, element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome, element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
	                 r.find (x => x.equals(mrd)) match {
	                	 case Some(x) => {
	                		 lst.find { iter =>
				                 mrd.uploadYear == iter.uploadYear && mrd.uploadMonth == iter.uploadMonth && mrd.minimumUnitCh == iter.minimumUnitCh && mrd.hospId == iter.hospNum
			                 }.map { y => 
			                	 x.sumValue = y.sumValue
								 x.volumeUnit = y.volumeUnit
			                 }.getOrElse(Unit)
	                	 }
	                	 case None => {
				             lst.find { iter =>
				                 mrd.uploadYear == iter.uploadYear && mrd.uploadMonth == iter.uploadMonth && mrd.minimumUnitCh == iter.minimumUnitCh && mrd.hospId == iter.hospNum
			                 }.map { y => 
			                	 mrd.sumValue = y.sumValue
								 mrd.volumeUnit = y.volumeUnit
			                 }.getOrElse(Unit)
			                 r.append(mrd)
	                	 }
	                 }
	             }
	        })
		}
		case SplitGroupMaster.mappingend() => {
			println(s"mapping size is ${r.size} in context $self")
			lazy val maxSum = new maxSumData()(r.toStream).toList
			aggregator ! SplitWorker.requestaverage(maxSum)	
		}
		case SplitEventBus.average(avg) => {
	    	lazy val calc = new maxCalcData()(r.toList, avg)
	    	val result = calc.groupBy (x => (x.uploadYear, x.uploadMonth)) map { x =>
				    (DateUtil.getDateLong(x._1._1,x._1._2), (x._2 map(_.finalResultsValue) sum, x._2 map(_.finalResultsUnit) sum))
				}
	    	aggregator ! SplitWorker.postresult(result)
	    }
		case _ => Unit
	}
}