package com.pharbers.aqll.calc.excel.model

import scala.beans.BeanProperty
import com.pharbers.aqll.calc.util.StringOption

abstract class modelRunData(
             val company :String,
             val uploadYear :Int,
             val uploadMonth :Int,
             var sumValue :Double,
             var volumeUnit :Double,
             val minimumUnit :String,
             val minimumUnitCh :String,
             val minimumUnitEn :String,
             val manufacturerCh :String,
             val manufacturerEn :String,
             val generalnameCh :String,
             val generalnameEn :String,
             val tradenameCh :String,
             val tradenameEn :String,
             val dosageformsCh :String,
             val dosageformsEn :String,
             val drugspecificationsCh :String,
             val drugspecificationsEn :String,
             val numberPackagingCh :String,
             val numberPackagingEn :String,
             val skuCh :String,
             val skuEn :String,
             val market1Ch :String,
             val market1En :String,
             val segment :String,
             val factor :String,
             val ifPanelAll :String,
             val ifPanelTouse :String,
             val hospId :Long,
             val hospName :String,
             val phaid :String,
             val ifCounty :String,
             val hospLevel :String,
             val region :String,
             val province :String,
             val prefecture :String,
             val cityTier :String,
             val specialty1 :String,
             val specialty2 :String,
             val reSpecialty :String,
             val specialty3 :String,
             val westMedicineIncome :Double,
             val doctorNum :Long,
             val bedNum :Long,
             val generalBedNum :Long,
             val medicineBedNum :Long,
             val surgeryBedNum :Long,
             val ophthalmologyBedNum :Long,
             val yearDiagnosisNum :Long,
             val clinicNum :Long,
             val medicineNum :Long,
             val surgeryNum :Long,
             val hospitalizedNum :Long,
             val hospitalizedOpsNum :Long,
             val income :Double,
             val clinicIncome :Double,
             val climicCureIncome :Double,
             val hospitalizedIncome :Double,
             val hospitalizedBeiIncome :Double,
             val hospitalizedCireIncom :Double,
             val hospitalizedOpsIncome	:Double,
             val drugIncome	:Double,
             val climicDrugIncome	:Double,
             val climicWestenIncome	:Double,
             val hospitalizedDrugIncome	:Double,
             val hospitalizedWestenIncome	:Double,
             var finalResultsValue	:Double,
             var finalResultsUnit	:Double
             ) extends java.io.Serializable {
      
      def selectvariablecalculation(): Option[(String, Double)] = None
    
//      override def toString =  "segment = "+segment.toString+
//                                     "uploadYear = "+uploadYear.toString()+
//                                     ",uploadMonth = "+uploadMonth.toString()+
//                                     ",minimumUnitEn = "+minimumUnitEn+
//                                     ",ifPanelTouse = "+ifPanelTouse+
//                                     ",hospId = "+hospId.toString+
//                                     ",SumValue = "+sumValue.toString()+
//                                     ",volumeUnit = "+volumeUnit.toString()
//                                     finalResultsValue.toString()+ "===" +finalResultsUnit.toString()
        ////                             "uploadYear = "+uploadYear.toString()+"，uploadMonth = "+uploadMonth.toString()+"，minimumUnitCh = "+minimumUnitCh+"，hospId = "+hospId
            
            lazy val sortConditions1 : String = uploadYear.toString + uploadMonth.toString + hospId.toString + StringOption.takeStringSpace(minimumUnitCh.toString)
        //    MD5.md5(uploadYear.toString + uploadMonth.toString + hospId.toString + StringOption.takeStringSpace(minimumUnitCh.toString))
        //    def equals(other : modelRunData) : Boolean =
        //    	this.uploadYear	== other.uploadYear &&
        //	    this.uploadMonth == other.uploadMonth &&
        //    	this.minimumUnit == other.minimumUnit &&
        //	    this.segment == other.segment &&
        //	    this.factor	== other.factor &&
        //	    this.ifPanelAll	== other.ifPanelAll &&
        //	    this.ifPanelTouse == other.ifPanelTouse &&
        //		this.hospId	== other.hospId &&
        //	    this.phaid == other.phaid
}