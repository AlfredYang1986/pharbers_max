package com.pharbers.aqll.calc.excel.model

import scala.beans.BeanProperty
import com.pharbers.aqll.calc.util.StringOption

class modelRunData  (@BeanProperty val	company	:String,
    @BeanProperty val	uploadYear	:Int,
    @BeanProperty val	uploadMonth	:Int,
    @BeanProperty var	sumValue	:Double,
    @BeanProperty var	volumeUnit	:Double,
    @BeanProperty val	minimumUnit	:String,
    @BeanProperty val	minimumUnitCh	:String,
    @BeanProperty val	minimumUnitEn	:String,
    @BeanProperty val	manufacturerCh	:String,
    @BeanProperty val	manufacturerEn	:String,
    @BeanProperty val	generalnameCh	:String,
    @BeanProperty val	generalnameEn	:String,
    @BeanProperty val	tradenameCh	:String,
    @BeanProperty val	tradenameEn	:String,
    @BeanProperty val	dosageformsCh	:String,
    @BeanProperty val	dosageformsEn	:String,
    @BeanProperty val	drugspecificationsCh	:String,
    @BeanProperty val	drugspecificationsEn	:String,
    @BeanProperty val	numberPackagingCh	:String,
    @BeanProperty val	numberPackagingEn	:String,
    @BeanProperty val	skuCh	:String,
    @BeanProperty val	skuEn	:String,
    @BeanProperty val	market1Ch	:String,
    @BeanProperty val	market1En	:String,
    @BeanProperty val	segment	:String,
    @BeanProperty val	factor	:String,
    @BeanProperty val	ifPanelAll	:String,
    @BeanProperty val	ifPanelTouse	:String,
    @BeanProperty val	hospId	:Long,
    @BeanProperty val	hospName	:String,
    @BeanProperty val	phaid	:String,
    @BeanProperty val	ifCounty	:String,
    @BeanProperty val	hospLevel	:String,
    @BeanProperty val	region	:String,
    @BeanProperty val	province	:String,
    @BeanProperty val	prefecture	:String,
    @BeanProperty val	cityTier	:String,
    @BeanProperty val	specialty1	:String,
    @BeanProperty val	specialty2	:String,
    @BeanProperty val	reSpecialty	:String,
    @BeanProperty val	specialty3	:String,
    @BeanProperty val	westMedicineIncome	:Double,
    @BeanProperty val	doctorNum	:Long,
    @BeanProperty val	bedNum	:Long,
    @BeanProperty val	generalBedNum	:Long,
    @BeanProperty val	medicineBedNum	:Long,
    @BeanProperty val	surgeryBedNum	:Long,
    @BeanProperty val	ophthalmologyBedNum	:Long,
    @BeanProperty val	yearDiagnosisNum	:Long,
    @BeanProperty val	clinicNum	:Long,
    @BeanProperty val	medicineNum	:Long,
    @BeanProperty val	surgeryNum	:Long,
    @BeanProperty val	hospitalizedNum	:Long,
    @BeanProperty val	hospitalizedOpsNum	:Long,
    @BeanProperty val	income	:String,
    @BeanProperty val	clinicIncome	:String,
    @BeanProperty val	climicCureIncome	:String,
    @BeanProperty val	hospitalizedIncome	:String,
    @BeanProperty val	hospitalizedBeiIncome	:String,
    @BeanProperty val	hospitalizedCireIncom	:String,
    @BeanProperty val	hospitalizedOpsIncome	:String,
    @BeanProperty val	drugIncome	:String,
    @BeanProperty val	climicDrugIncome	:String,
    @BeanProperty val	climicWestenIncome	:String,
    @BeanProperty val	hospitalizedDrugIncome	:String,
    @BeanProperty val	hospitalizedWestenIncome	:String,
    @BeanProperty var	finalResultsValue	:Double,
    @BeanProperty var	finalResultsUnit	:Double) extends java.io.Serializable {
  
    override def toString =  "segment = "+segment.toString+
                             "uploadYear = "+uploadYear.toString()+
                             ",uploadMonth = "+uploadMonth.toString()+
                             ",minimumUnitEn = "+minimumUnitEn+
                             ",ifPanelTouse = "+ifPanelTouse+
                             ",hospId = "+hospId.toString+
                             ",SumValue = "+sumValue.toString()+
                             ",volumeUnit = "+volumeUnit.toString()
//                             finalResultsValue.toString()+ "===" +finalResultsUnit.toString()
//                             "uploadYear = "+uploadYear.toString()+"，uploadMonth = "+uploadMonth.toString()+"，minimumUnitCh = "+minimumUnitCh+"，hospId = "+hospId
                             
                             
    lazy val sortConditions1 : String = uploadYear.toString + uploadMonth.toString + hospId.toString + StringOption.takeStringSpace(minimumUnitCh.toString)
//      MD5.md5(uploadYear.toString + uploadMonth.toString + hospId.toString + StringOption.takeStringSpace(minimumUnitCh.toString))
}