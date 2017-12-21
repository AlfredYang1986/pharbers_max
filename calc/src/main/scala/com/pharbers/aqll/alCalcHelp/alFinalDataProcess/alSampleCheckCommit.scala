//package com.pharbers.aqll.alCalcOther.alfinaldataprocess
//
//import com.mongodb.casbah.Imports._
//import com.pharbers.aqll.alCalcHelp.dbcores._
//import com.mongodb.casbah.commons.MongoDBObject
//import com.pharbers.aqll.common.alErrorCode.alErrorCode._
//import play.api.libs.json.JsValue
//import play.api.libs.json.Json.toJson
//
///**
//  * Created by liwei on 2017/4/18.
//  */
//case class alSampleCheckCommit() {
//
//  def apply(company: String): JsValue = {
//    try {
//      dbc.getCollection("FactResult").find(MongoDBObject("Company" -> company)).toList.foreach{c =>
//        dbc.getCollection("SampleCheckResult").findAndRemove(MongoDBObject("ID" -> c.get("ID")))
//        dbc.getCollection("SampleCheckResult").insert(
//          Map(
//            "ID" -> c.get("ID"),
//            "Date" -> c.get("Date"),
//            "Market" -> c.get("Market"),
//            "Company" -> c.get("Company"),
//            "HospNum" -> c.get("HospNum"),
//            "ProductNum" -> c.get("ProductNum"),
//            "MarketNum" -> c.get("MarketNum"),
//            "Units" -> c.get("Units"),
//            "Sales" -> c.get("Sales")
//          )
//        )
//        dbc.getCollection("FactResult").findAndRemove(MongoDBObject("Company" -> c.get("Company")))
//      }
//      toJson(successToJson().get)
//    } catch {
//      case ex: Exception => errorToJson(ex.getMessage)
//    }
//  }
//}