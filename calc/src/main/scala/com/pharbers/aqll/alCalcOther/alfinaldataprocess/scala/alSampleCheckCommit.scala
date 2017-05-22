package com.pharbers.aqll.alCalcOther.alfinaldataprocess.scala

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.alCalaHelp.DBList

/**
  * Created by liwei on 2017/4/18.
  */
object alSampleCheckCommit {
  def apply(company: String): alSampleCheckCommit = new alSampleCheckCommit(company)
}

class alSampleCheckCommit(company: String) extends DBList{
  try {
    val lst_temp = dbcores.getCollection("FactResult").find(new MongoDBObject(MongoDBObject("Company" -> company)))
    while(lst_temp.hasNext) {
      val c : DBObject = lst_temp.next()
      dbcores.getCollection("SampleCheckResult").findAndRemove(
        new MongoDBObject(MongoDBObject(
          "ID" -> c.get("ID"),
          "Date" -> c.get("Date"),
          "Market" -> c.get("Market"),
          "Company" -> c.get("Company"),
          "HospNum" -> c.get("HospNum"),
          "ProductNum" -> c.get("ProductNum"),
          "MarketNum" -> c.get("MarketNum"),
          "Units" -> c.get("Units"),
          "Sales" -> c.get("Sales")
        ))
      )
      dbcores.getCollection("SampleCheckResult").insert(
        Map(
          "ID" -> c.get("ID"),
          "Date" -> c.get("Date"),
          "Market" -> c.get("Market"),
          "Company" -> c.get("Company"),
          "HospNum" -> c.get("HospNum"),
          "ProductNum" -> c.get("ProductNum"),
          "MarketNum" -> c.get("MarketNum"),
          "Units" -> c.get("Units"),
          "Sales" -> c.get("Sales")
        )
      )
      dbcores.getCollection("FactResult").findAndRemove(new MongoDBObject(MongoDBObject("Company" -> c.get("Company"))))
    }
  } catch {
    case e: Exception => println(e.getMessage)
  }
}