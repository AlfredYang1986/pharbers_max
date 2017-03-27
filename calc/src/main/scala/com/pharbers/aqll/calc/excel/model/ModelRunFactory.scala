package com.pharbers.aqll.calc.excel.model

import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData

class ModelRunFactory() {
	def apply(map: Map[String, Any], output: Stream[((Integer, String), IntegratedData)]) = {
		map.get("calcvariable").get.asInstanceOf[Int] match {
			case 0 => westMedicineIncome(map.get("hospdatapath").get.toString, output)
			case _ => ???
		}
	}

	def westMedicineIncome(hospDataBasePath: String, output: Stream[((Integer, String), IntegratedData)]): Stream[modelRunData] = {
		var index = 0
		println(s"output.size = ${output.size}")
		lazy val hospdata = DefaultData.hospdatabase(hospDataBasePath, "")
		//val o = output.sortBy{ x => (x._2.getYearAndmonth.toString.substring(0, 4), x._2.getMarket1Ch)}
		(output.map { x =>
			index = index + 1
			println(s"current precentage : ${index} / ${output.size}")
			val element2 = x._2
			hospdata map { element =>
				new westMedicineIncome(element.getCompany, element2.getYearAndmonth, 0, 0, element2.getMinimumUnit, element2.getMinimumUnitCh, element2.getMinimumUnitEn, element2.getMarket1Ch, element2.getMarket1En, element.getSegment, element.getFactor, element.getIfPanelAll, element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid, element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince, element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2, element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum, element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum, element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum, element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome, element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome, element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome, element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome, element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
			}
		}).flatten
	}

}