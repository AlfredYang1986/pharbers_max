package com.pharbers.aqll.alCalcOther.alfinaldataprocess

import java.util.{Date, UUID}

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.alCalaHelp.dbcores._
import com.pharbers.aqll.alCalaHelp.DefaultData
import com.pharbers.aqll.alCalc.almodel.java.AdminHospitalDataBase
import com.pharbers.aqll.alCalcOther.alMessgae.{alWebSocket}
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alString.alStringOpt._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map
import scala.collection.mutable.ListBuffer

/**
  * Created by liwei on 2017/3/27.
  */
case class alSampleCheck() {

  def apply(company: String, filename: String, uname: String): JsValue = {
    try {
      val panels = DefaultData.integratedbase(filename, company)
      val dates = panels.groupBy(x => x.getYearAndmonth)
      dates.foreach{date =>
        val Panels_Filter_Ym = panels.filter(x => x.getYearAndmonth.equals(date._1))
        val msg = Map(
          "type" -> "progress",
          "progress" -> "5"
        )
        alWebSocket(uname).post(msg)
//        new alMessageProxy().sendMsg("5", uname, Map("uuid" -> "", "company" -> company, "type" -> "progress"))
        val Panels_Group_Pha = Panels_Filter_Ym.groupBy(x => x.getPhaid).map(y => (y._1,y._2.size)).toList
        val Market_Current = Panels_Filter_Ym.groupBy(x => x.getMarket1Ch)
        Market_Current.foreach{mc =>
          val hospdata = DefaultData.hospdatabase(alEncryptionOpt.md5(company+date._1.toString.substring(0,4)+removeSpace(mc._1)), company)
          var HospNum,ProductNum = 0
          var Sales,Units = 0.0
          val mismatch = new ListBuffer[List[String]]()

          hospdata.asInstanceOf[List[AdminHospitalDataBase]].foreach { x =>
            if(x.getIfPanelAll.equals("1")){
              if(Panels_Group_Pha.exists(y => y._1.equals(x.getPhaid))){
                ProductNum = ProductNum + Panels_Filter_Ym.filter(j => j.getPhaid.equals(x.getPhaid)).size
                var Sales_S,Units_S = 0.0
                Panels_Filter_Ym.filter(j => j.getPhaid.equals(x.getPhaid)).foreach{su =>
                  Sales_S = Sales_S + su.getSumValue
                  Units_S = Units_S + su.getVolumeUnit
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
  
          dbc.getCollection("FactResult").findAndRemove(MongoDBObject(
            "Company" -> company,
            "Market" -> mc._1,
            "Date" -> alDateOpt.yyyyMM2Long(date._1.toString)))
  
          dbc.getCollection("FactResult").insert(MongoDBObject(
            "ID" -> alEncryptionOpt.md5(UUID.randomUUID().toString),
            "Date" -> alDateOpt.yyyyMM2Long(date._1.toString),
            "Market" -> mc._1,
            "Company" -> company,
            "HospNum" -> HospNum,
            "ProductNum" -> ProductNum,
            "MarketNum" -> Market_Current.size,
            "Units" -> Units,
            "Sales" -> Sales,
            "Mismatch" -> lsb.toList,
            "CreateDate" -> alDateOpt.Date2Long(new Date())))
        }
      }
      alFileOpt(fileBase + company + client_cpa_file).removeCurFiles
      alFileOpt(fileBase + company + client_gycx_file).removeCurFiles
      toJson(successToJson().get)
    } catch {
      case e: Exception =>
        println(e)
        errorToJson(e.getMessage)
    }
  }
}
