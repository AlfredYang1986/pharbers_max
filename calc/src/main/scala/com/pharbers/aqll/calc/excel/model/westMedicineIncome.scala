package com.pharbers.aqll.calc.excel.model


class westMedicineIncome(
	                        company: String,
	                        yearAadmonth: Int,
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
	yearAadmonth: Int,
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
	override def selectvariablecalculation(): Option[(String, Double)] = {
		Some("西药收入", westMedicineIncome)
	}

	override def toString: String =
		company + "," +
		yearAadmonth + "," +
		sumValue + "," +
		volumeUnit + "," +
		minimumUnit + "," +
    	minimumUnitCh + "," +
    	minimumUnitEn + "," +
	    market1Ch + "," +
    	market1En + "," +
    	segment + "," +
    	factor + "," +
	    ifPanelAll + "," +
    	ifPanelTouse + "," +
    	hospId + "," +
    	hospName + "," +
    	phaid + "," +
    	ifCounty + "," +
    	hospLevel + "," +
    	region + "," +
    	province + "," +
    	prefecture + "," +
    	cityTier + "," +
    	specialty1 + "," +
    	specialty2 + "," +
    	reSpecialty + "," +
    	specialty3 + "," +
    	westMedicineIncome + "," +
    	doctorNum + "," +
    	bedNum + "," +
    	generalBedNum + "," +
        medicineBedNum + "," +
        surgeryBedNum + "," +
        ophthalmologyBedNum + "," +
        yearDiagnosisNum + "," +
        clinicNum + "," +
        medicineNum + "," +
        surgeryNum + "," +
        hospitalizedNum + "," +
        hospitalizedOpsNum + "," +
        income + "," +
        clinicIncome + "," +
        climicCureIncome + "," +
        hospitalizedIncome + "," +
        hospitalizedBeiIncome + "," +
        hospitalizedCireIncom + "," +
        hospitalizedOpsIncome + "," +
        drugIncome + "," +
        climicDrugIncome + "," +
        climicWestenIncome + "," +
        hospitalizedDrugIncome + "," +
        hospitalizedWestenIncome + "," +
        finalResultsValue + "," +
        finalResultsUnit
}
