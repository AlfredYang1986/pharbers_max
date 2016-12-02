package com.pharbers.aqll.calc.excel.model

import scala.beans.BeanProperty
import com.pharbers.aqll.calc.excel.helpFunc.StringOption

class integratedData(@BeanProperty val uploadYear : Int,
    @BeanProperty val uploadMonth : Int,
    @BeanProperty val dataSource : String,
    @BeanProperty val hospNum : Long,
    @BeanProperty var sumValue : Double,
    @BeanProperty var volumeUnit : Double,
    @BeanProperty val minimumUnit : String,
    @BeanProperty val minimumUnitCh : String,
    @BeanProperty val minimumUnitEn : String,
    @BeanProperty val manufacturerCh : String,
    @BeanProperty val manufacturerEn : String,
    @BeanProperty val generalnameCh : String,
    @BeanProperty val generalnameEn : String,
    @BeanProperty val tradenameCh : String,
    @BeanProperty val tradenameEn : String,
    @BeanProperty val dosageformsCh : String,
    @BeanProperty val dosageformsEn : String,
    @BeanProperty val drugspecificationsCh : String,
    @BeanProperty val drugspecificationsEn : String,
    @BeanProperty val numberpackagingCh : String,
    @BeanProperty val numberpackagingEn : String,
    @BeanProperty val skuCh : String,
    @BeanProperty val skuEn : String,
    @BeanProperty val market1Ch : String,
    @BeanProperty val market1En : String,
    @BeanProperty val hospNameCh : String,
    @BeanProperty val hospNameEn : String,
    @BeanProperty val hospLevelCh : String,
    @BeanProperty val hospLevelEn : String,
    @BeanProperty val areaCh : String,
    @BeanProperty val areaEn : String,
    @BeanProperty val provinceCh : String,
    @BeanProperty val provinceEn : String,
    @BeanProperty val cityCh : String,
    @BeanProperty val cityEn : String) {
    
    override def toString = ""+uploadYear.toString()+
                             "	"+uploadMonth.toString()+
                             "	"+StringOption.takeStringSpace(minimumUnit)+
                             "	"+hospNum.toString()+
                             "	"+sumValue.toString()+
                             "	"+volumeUnit.toString()
                             
  lazy val sortConditions1 : String = uploadYear.toString + uploadMonth.toString + hospNum.toString + StringOption.takeStringSpace(minimumUnitCh.toString)
//    MD5.md5(uploadYear.toString + uploadMonth.toString + hospNum.toString + StringOption.takeStringSpace(minimumUnitCh.toString)) 
}