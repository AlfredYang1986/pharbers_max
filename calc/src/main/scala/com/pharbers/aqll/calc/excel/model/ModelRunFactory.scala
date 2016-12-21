package com.pharbers.aqll.calc.excel.model

import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.excel.Manage.AdminHospitalDataBase

//trait ModelRunTrait {
//    def modelrun(element: AdminHospitalDataBase, element2: integratedData): modelRunData = null
//    
//    def modelrundata(output: Stream[((Int, Int, String), integratedData)]): Stream[modelRunData]
//}
//
//abstract class ModelRunAbstract extends ModelRunTrait

//case class ModelRunDefines() extends ModelRunAbstract {
//    
//    override def modelrundata(output: Stream[((Int, Int, String), integratedData)]): Stream[modelRunData] = {
//        var index = 0
//        (output.map { x =>
//        	index = index + 1
//        	println(s"current precentage : ${index} / ${output.size}")
//        	val element2 = x._2
//        	DefaultData.hospdatabase map { element =>
//        	    modelrun(element, element2)
//        	}
//        }).flatten
//    }
//    
//}

class ModelRunFactory(){
    def apply(n: Int, output: Stream[((Int, Int, String), integratedData)]) = {
        n match {
            case 0 => westMedicineIncome(output)
            case _ => ???
        }
    }
    
    def westMedicineIncome(output: Stream[((Int, Int, String), integratedData)]): Stream[modelRunData] = {
        var index = 0
        (output.map { x =>
        	index = index + 1
        	println(s"current precentage : ${index} / ${output.size}")
        	val element2 = x._2
        	DefaultData.hospdatabase map { element =>
        	    new westMedicineIncome(element.getCompany, element2.uploadYear, element2.uploadMonth, 0.0, 0.0, element2.minimumUnit, element2.minimumUnitCh, element2.minimumUnitEn, element2.manufacturerCh, element2.manufacturerEn, element2.generalnameCh, element2.generalnameEn, element2.tradenameCh, element2.tradenameEn, element2.dosageformsCh, element2.dosageformsEn, element2.drugspecificationsCh, element2.drugspecificationsEn, element2.numberpackagingCh, element2.numberpackagingEn, element2.skuCh, element2.skuEn, element2.market1Ch, element2.market1En, element.getSegment, element.getFactor, element.getIfPanelAll, element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid, element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince, element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2, element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum, element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum, element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum, element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome, element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome, element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome, element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome, element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
        	}
        }).flatten
    }
    
}