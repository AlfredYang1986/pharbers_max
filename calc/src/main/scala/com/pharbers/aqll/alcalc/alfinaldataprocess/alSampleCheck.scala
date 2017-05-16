package com.pharbers.aqll.alcalc.alfinaldataprocess

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.alcalc.alCommon.DefaultData
import com.pharbers.aqll.alcalc.almodel.AdminHospitalDataBase
import java.util.UUID

import scala.collection.mutable.ListBuffer
import scala.collection.immutable.List
import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.alcalc.alemchat.sendMessage
import java.io._

import com.pharbers.aqll.common.alDao._data_connection_cores
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import com.pharbers.aqll.old.calc.util.{DateUtils, StringOption}
import com.pharbers.aqll.util.fileConfig._
/**
  * Created by liwei on 2017/3/27.
  */
object alSampleCheck {
  def apply(company: String, filename: String, uname: String): alSampleCheck = new alSampleCheck(company, filename, uname)
}

class alSampleCheck(company : String, filename : String, uname: String) {
  try {
    val panels = DefaultData.integratedbase(filename, company)
    val dates = panels.groupBy(x => x.getYearAndmonth)
    dates.foreach{date =>
      val Panels_Filter_Ym = panels.filter(x => x.getYearAndmonth.equals(date._1))
      sendMessage.sendMsg("10", uname, Map("uuid" -> "", "company" -> company, "type" -> "progress"))
      val Panels_Group_Pha = Panels_Filter_Ym.groupBy(x => x.getPhaid).map(y => (y._1,y._2.size)).toList
      val Market_Current = Panels_Filter_Ym.groupBy(x => x.getMarket1Ch)
      //println(s"Date =${date._1}")

      Market_Current.foreach{mc =>

        val hospdata = DefaultData.hospdatabase(alEncryptionOpt.md5(company+date._1.toString.substring(0,4)+StringOption.takeStringSpace(mc._1)), company)
        var HospNum,ProductNum,Sales,Units = 0.0
        val mismatch = new ListBuffer[List[String]]()

        hospdata.asInstanceOf[List[AdminHospitalDataBase]].foreach { x =>
          if(x.getIfPanelAll.equals("1")){
            if(Panels_Group_Pha.exists(y => y._1.equals(x.getPhaid))){
              ProductNum = ProductNum + Panels_Filter_Ym.filter(j => j.getPhaid.equals(x.getPhaid)).size
              var Sales_S,Units_S = 0.0
              Panels_Filter_Ym.filter(j => j.getPhaid.equals(x.getPhaid)).foreach{su =>
                Sales_S = Sales_S + su.getSumValue
                Units_S = Units_S + su.getVolumeUnit
                //println(s"Sales_S=${su.getSumValue} Units_S=${su.getVolumeUnit}")
              }
              Sales = Sales + Sales_S
              Units = Units + Units_S
              HospNum = HospNum + 1
            }else{
              mismatch.append(List(x.getHospName,x.getProvince,x.getPrefecture,x.getCityTier))
            }
          }
        }
        val lsb = new ListBuffer[Map[String,String]]()
        mismatch.toList.foreach(x => lsb.append(Map("Hosp_name" -> x.head,"Province" -> x.tail.head,"City" -> x.tail.tail.head,"City_level" -> x.tail.tail.tail.head)))
        _data_connection_cores.getCollection("FactResult").findAndRemove(new MongoDBObject(MongoDBObject("Company" -> company,"Market" -> mc._1,"Date" -> alDateOpt.yyyyMM2Long(date._1.toString))))
        _data_connection_cores.getCollection("FactResult").insert(Map("ID" -> alEncryptionOpt.md5(UUID.randomUUID().toString),"Date" -> alDateOpt.yyyyMM2Long(date._1.toString),"Market" -> mc._1,"Company" -> company,"HospNum" -> HospNum.toInt,"ProductNum" -> ProductNum.toInt,"MarketNum" -> Market_Current.size,"Units" -> Units,"Sales" -> Sales,"Mismatch" -> lsb.toList,"CreateDate" -> DateUtils.Date2Long(new Date())))
        //println(s"日期：${date._1} 市场：${mc._1} 公司：${company} 医院数量：${HospNum.toInt} 产品数量：${ProductNum.toInt} 市场数量：${Market_Current.size} 销售额：${Sales} 销售数量：${Units} 未匹配：${mismatch.toList.size}")
      }
    }
    alFileOpt(fileBase+company+client_cpa_file).removeCurFiles
    alFileOpt(fileBase+company+client_gycx_file).removeCurFiles
//    FileUtils.delFolder(fileBase+company+manage_file)
  } catch {
    case e: Exception => println(e.getMessage)
    case ioe: IOException => println(ioe.getMessage)
  }
}
