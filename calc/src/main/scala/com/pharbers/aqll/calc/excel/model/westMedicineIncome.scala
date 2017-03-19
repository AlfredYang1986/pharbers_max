package com.pharbers.aqll.calc.excel.model


/**
  * 这里是老子见过最稀烂的Scala代码
  * Alfred
  */

class westMedicineIncome(
	                        company: String,
	                        yearAndmonth: Int,
	                        sumValue: Double,
	                        volumeUnit: Double,
	                        minimumUnit: String,
	                        minimumUnitCh: String,
	                        minimumUnitEn: String,
	                        market1Ch: String,
	                        market1En: String,
	                        segment: String,
	                        factor: String,
	                        ifPanelAll: String,
	                        ifPanelTouse: String,
	                        hospId: Long,
	                        hospName: String,
	                        phaid: String,
	                        ifCounty: String,
	                        hospLevel: String,
	                        region: String,
	                        province: String,
	                        prefecture: String,
	                        cityTier: String,
	                        specialty1: String,
	                        specialty2: String,
	                        reSpecialty: String,
	                        specialty3: String,
	                        westMedicineIncome: Double,
	                        doctorNum: Long,
	                        bedNum: Long,
	                        generalBedNum: Long,
	                        medicineBedNum: Long,
	                        surgeryBedNum: Long,
	                        ophthalmologyBedNum: Long,
	                        yearDiagnosisNum: Long,
	                        clinicNum: Long,
	                        medicineNum: Long,
	                        surgeryNum: Long,
	                        hospitalizedNum: Long,
	                        hospitalizedOpsNum: Long,
	                        income: Double,
	                        clinicIncome: Double,
	                        climicCureIncome: Double,
	                        hospitalizedIncome: Double,
	                        hospitalizedBeiIncome: Double,
	                        hospitalizedCireIncom: Double,
	                        hospitalizedOpsIncome: Double,
	                        drugIncome: Double,
	                        climicDrugIncome: Double,
	                        climicWestenIncome: Double,
	                        hospitalizedDrugIncome: Double,
	                        hospitalizedWestenIncome: Double,
	                        finalResultsValue: Double,
	                        finalResultsUnit: Double
                        ) extends modelRunData(
	company: String,
	yearAndmonth: Int,
	sumValue: Double,
	volumeUnit: Double,
	minimumUnit: String,
	minimumUnitCh: String,
	minimumUnitEn: String,
	market1Ch: String,
	market1En: String,
	segment: String,
	factor: String,
	ifPanelAll: String,
	ifPanelTouse: String,
	hospId: Long,
	hospName: String,
	phaid: String,
	ifCounty: String,
	hospLevel: String,
	region: String,
	province: String,
	prefecture: String,
	cityTier: String,
	specialty1: String,
	specialty2: String,
	reSpecialty: String,
	specialty3: String,
	westMedicineIncome: Double,
	doctorNum: Long,
	bedNum: Long,
	generalBedNum: Long,
	medicineBedNum: Long,
	surgeryBedNum: Long,
	ophthalmologyBedNum: Long,
	yearDiagnosisNum: Long,
	clinicNum: Long,
	medicineNum: Long,
	surgeryNum: Long,
	hospitalizedNum: Long,
	hospitalizedOpsNum: Long,
	income: Double,
	clinicIncome: Double,
	climicCureIncome: Double,
	hospitalizedIncome: Double,
	hospitalizedBeiIncome: Double,
	hospitalizedCireIncom: Double,
	hospitalizedOpsIncome: Double,
	drugIncome: Double,
	climicDrugIncome: Double,
	climicWestenIncome: Double,
	hospitalizedDrugIncome: Double,
	hospitalizedWestenIncome: Double,
	finalResultsValue: Double,
	finalResultsUnit: Double
) {

	def copy(): westMedicineIncome = {
		val m: westMedicineIncome = this
		new westMedicineIncome(m.company, m.yearAndmonth, m.sumValue, m.volumeUnit, m.minimumUnit,
			m.minimumUnitCh, m.market1En, m.market1Ch,
			m.market1En, m.segment, m.factor, m.ifPanelAll,
			m.ifPanelTouse, m.hospId, m.hospName, m.phaid,
			m.ifCounty, m.hospLevel, m.region, m.province,
			m.prefecture, m.cityTier, m.specialty1, m.specialty2,
			m.reSpecialty, m.specialty3, m.westMedicineIncome, m.doctorNum,
			m.bedNum, m.generalBedNum, m.medicineBedNum, m.surgeryBedNum,
			m.ophthalmologyBedNum, m.yearDiagnosisNum, m.clinicNum, m.medicineNum,
			m.surgeryNum, m.hospitalizedNum, m.hospitalizedOpsNum, m.income,
			m.clinicIncome, m.climicCureIncome, m.hospitalizedIncome,
			m.hospitalizedBeiIncome, m.hospitalizedCireIncom, m.hospitalizedOpsIncome,
			m.drugIncome, m.climicDrugIncome, m.climicWestenIncome,
			m.hospitalizedDrugIncome, m.hospitalizedWestenIncome, m.finalResultsValue, m.finalResultsUnit)
	}

	override def selectvariablecalculation(): Option[(String, Double)] = {
		Some("西药收入", westMedicineIncome)
	}

	override def toString: String = {
		"company" + "=" + company + 31.toChar +
			"yearAndmonth" + "=" + yearAndmonth + 31.toChar +
			"sumValue" + "=" + sumValue + 31.toChar +
			"volumeUnit" + "=" + volumeUnit + 31.toChar +
			"minimumUnit" + "=" + minimumUnit + 31.toChar +
			"minimumUnitCh" + "=" + minimumUnitCh + 31.toChar +
			"minimumUnitEn" + "=" + minimumUnitEn + 31.toChar +
			"market1Ch" + "=" + market1Ch + 31.toChar +
			"market1En" + "=" + market1En + 31.toChar +
			"segment" + "=" + segment + 31.toChar +
			"factor" + "=" + factor + 31.toChar +
			"ifPanelAll" + "=" + ifPanelAll + 31.toChar +
			"ifPanelTouse" + "=" + ifPanelTouse + 31.toChar +
			"hospId" + "=" + hospId + 31.toChar +
			"hospName" + "=" + hospName + 31.toChar +
			"phaid" + "=" + phaid + 31.toChar +
			"ifCounty" + "=" + ifCounty + 31.toChar +
			"hospLevel" + "=" + hospLevel + 31.toChar +
			"region" + "=" + region + 31.toChar +
			"province" + "=" + province + 31.toChar +
			"prefecture" + "=" + prefecture + 31.toChar +
			"cityTier" + "=" + cityTier + 31.toChar +
			"specialty1" + "=" + specialty1 + 31.toChar +
			"specialty2" + "=" + specialty2 + 31.toChar +
			"reSpecialty" + "=" + reSpecialty + 31.toChar +
			"specialty3" + "=" + specialty3 + 31.toChar +
			"westMedicineIncome" + "=" + westMedicineIncome + 31.toChar +
			"doctorNum" + "=" + doctorNum + 31.toChar +
			"bedNum" + "=" + bedNum + 31.toChar +
			"generalBedNum" + "=" + generalBedNum + 31.toChar +
			"medicineBedNum" + "=" + medicineBedNum + 31.toChar +
			"surgeryBedNum" + "=" + surgeryBedNum + 31.toChar +
			"ophthalmologyBedNum" + "=" + ophthalmologyBedNum + 31.toChar +
			"yearDiagnosisNum" + "=" + +31.toChar +
			"clinicNum" + "=" + yearDiagnosisNum + 31.toChar +
			"medicineNum" + "=" + medicineNum + 31.toChar +
			"surgeryNum" + "=" + surgeryNum + 31.toChar +
			"hospitalizedNum" + "=" + +31.toChar +
			"hospitalizedOpsNum" + "=" + hospitalizedNum + 31.toChar +
			"income" + "=" + income + 31.toChar +
			"clinicIncome" + "=" + clinicIncome + 31.toChar +
			"climicCureIncome" + "=" + climicCureIncome + 31.toChar +
			"hospitalizedIncome" + "=" + hospitalizedIncome + 31.toChar +
			"hospitalizedBeiIncome" + "=" + hospitalizedBeiIncome + 31.toChar +
			"hospitalizedCireIncom" + "=" + hospitalizedCireIncom + 31.toChar +
			"hospitalizedOpsIncome" + "=" + hospitalizedOpsIncome + 31.toChar +
			"drugIncome" + "=" + drugIncome + 31.toChar +
			"climicDrugIncome" + "=" + climicDrugIncome + 31.toChar +
			"climicWestenIncome" + "=" + climicWestenIncome + 31.toChar +
			"hospitalizedDrugIncome" + "=" + hospitalizedDrugIncome + 31.toChar +
			"hospitalizedWestenIncome" + "=" + hospitalizedWestenIncome + 31.toChar +
			"finalResultsValue" + "=" + finalResultsValue + 31.toChar +
			"finalResultsUnit" + "=" + finalResultsUnit
	}
}
