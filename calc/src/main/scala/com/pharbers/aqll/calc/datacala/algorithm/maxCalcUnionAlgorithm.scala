package com.pharbers.aqll.calc.datacala.algorithm

import com.pharbers.aqll.calc.excel.Manage.AdminHospitalDataBase
import com.pharbers.aqll.calc.excel.model.integratedData
import com.pharbers.aqll.calc.excel.model.modelRunData
import scala.collection.mutable.ArrayBuffer

class maxCalcUnionAlgorithm {
//     def apply(integratedData : Stream[integratedData], hosp_data_base : Stream[AdminHospitalDataBase])(f : modelRunData => Unit) = {  
//         val output = integratedData.toList.groupBy(x => (x.getUploadYear, x.getUploadMonth, x.getMinimumUnitCh)).map(_._2.head).toList
//         (output map { element2 =>
//             hosp_data_base.toList map { element =>
//                 val mrd = new modelRunData(element.getCompany, element2.uploadYear, element2.uploadMonth, 0.0, 0.0, element2.minimumUnit, element2.minimumUnitCh, element2.minimumUnitEn, element2.manufacturerCh, element2.manufacturerEn, element2.generalnameCh, element2.generalnameEn, element2.tradenameCh, element2.tradenameEn, element2.dosageformsCh, element2.dosageformsEn, element2.drugspecificationsCh, element2.drugspecificationsEn, element2.numberpackagingCh, element2.numberpackagingEn, element2.skuCh, element2.skuEn, element2.market1Ch, element2.market1En, element.getSegment, element.getFactor, element.getIfPanelAll, element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid, element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince, element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2, element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum, element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum, element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum, element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome, element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome, element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome, element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome, element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
//                 integratedData.find { iter =>
//	                 mrd.uploadYear == iter.uploadYear && mrd.uploadMonth == iter.uploadMonth && mrd.minimumUnitCh == iter.minimumUnitCh && mrd.hospId == iter.hospNum
//                 }.map { y => 
//                	 mrd.sumValue = y.sumValue
//					 mrd.volumeUnit = y.volumeUnit
//                 }.getOrElse(Unit)
//                 f(mrd)
//             }
//         })//.flatten
////         println(s"size max result ${a.size}")
//     }
    
    val r : ArrayBuffer[modelRunData] = ArrayBuffer.empty
    
    def apply(integratedData : Stream[integratedData], hosp_data_base : Stream[AdminHospitalDataBase]) = {
         val output = integratedData.toList.groupBy(x => (x.getUploadYear, x.getUploadMonth, x.getMinimumUnitCh)).map(_._2.head).toList
         (output map { element2 =>
             hosp_data_base.toList map { element =>
                 val mrd = new modelRunData(element.getCompany, element2.uploadYear, element2.uploadMonth, 0.0, 0.0, element2.minimumUnit, element2.minimumUnitCh, element2.minimumUnitEn, element2.manufacturerCh, element2.manufacturerEn, element2.generalnameCh, element2.generalnameEn, element2.tradenameCh, element2.tradenameEn, element2.dosageformsCh, element2.dosageformsEn, element2.drugspecificationsCh, element2.drugspecificationsEn, element2.numberpackagingCh, element2.numberpackagingEn, element2.skuCh, element2.skuEn, element2.market1Ch, element2.market1En, element.getSegment, element.getFactor, element.getIfPanelAll, element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid, element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince, element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2, element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum, element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum, element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum, element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome, element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome, element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome, element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome, element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
                 r.find (x => x.equals(mrd)) match {
                	 case Some(x) => {
                		 integratedData.find { iter =>
			                 mrd.uploadYear == iter.uploadYear && mrd.uploadMonth == iter.uploadMonth && mrd.minimumUnitCh == iter.minimumUnitCh && mrd.hospId == iter.hospNum
		                 }.map { y => 
		                	 x.sumValue = y.sumValue
							 x.volumeUnit = y.volumeUnit
		                 }.getOrElse(Unit)
                	 }
                	 case None => {
			             integratedData.find { iter =>
			                 mrd.uploadYear == iter.uploadYear && mrd.uploadMonth == iter.uploadMonth && mrd.minimumUnitCh == iter.minimumUnitCh && mrd.hospId == iter.hospNum
		                 }.map { y => 
		                	 mrd.sumValue = y.sumValue
							 mrd.volumeUnit = y.volumeUnit
		                 }.getOrElse(Unit)
		                 r.append(mrd)
                	 }
                 }
             }
             r
         }).flatten
     }
}