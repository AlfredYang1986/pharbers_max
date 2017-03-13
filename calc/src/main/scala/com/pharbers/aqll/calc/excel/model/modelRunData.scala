package com.pharbers.aqll.calc.excel.model

import scala.beans.BeanProperty
import com.pharbers.aqll.calc.util.StringOption

abstract class modelRunData(
	                           val company: String,
	                           val yearAndmonth: Int,
	                           var sumValue: Double,
	                           var volumeUnit: Double,
	                           val minimumUnit: String,
	                           val minimumUnitCh: String,
	                           val minimumUnitEn: String,
	                           val market1Ch: String,
	                           val market1En: String,
	                           val segment: String,
	                           val factor: String,
	                           val ifPanelAll: String,
	                           val ifPanelTouse: String,
	                           val hospId: Long,
	                           val hospName: String,
	                           val phaid: String,
	                           val ifCounty: String,
	                           val hospLevel: String,
	                           val region: String,
	                           val province: String,
	                           val prefecture: String,
	                           val cityTier: String,
	                           val specialty1: String,
	                           val specialty2: String,
	                           val reSpecialty: String,
	                           val specialty3: String,
	                           val westMedicineIncome: Double,
	                           val doctorNum: Long,
	                           val bedNum: Long,
	                           val generalBedNum: Long,
	                           val medicineBedNum: Long,
	                           val surgeryBedNum: Long,
	                           val ophthalmologyBedNum: Long,
	                           val yearDiagnosisNum: Long,
	                           val clinicNum: Long,
	                           val medicineNum: Long,
	                           val surgeryNum: Long,
	                           val hospitalizedNum: Long,
	                           val hospitalizedOpsNum: Long,
	                           val income: Double,
	                           val clinicIncome: Double,
	                           val climicCureIncome: Double,
	                           val hospitalizedIncome: Double,
	                           val hospitalizedBeiIncome: Double,
	                           val hospitalizedCireIncom: Double,
	                           val hospitalizedOpsIncome: Double,
	                           val drugIncome: Double,
	                           val climicDrugIncome: Double,
	                           val climicWestenIncome: Double,
	                           val hospitalizedDrugIncome: Double,
	                           val hospitalizedWestenIncome: Double,
	                           var finalResultsValue: Double,
	                           var finalResultsUnit: Double
                           ) { //extends java.io.Serializable {

	def selectvariablecalculation(): Option[(String, Double)] = None


	lazy val sortConditions1: String = yearAndmonth.toString + hospId.toString + StringOption.takeStringSpace(minimumUnitCh.toString)
	//    MD5.md5(uploadYear.toString + uploadMonth.toString + hospId.toString + StringOption.takeStringSpace(minimumUnitCh.toString))
}