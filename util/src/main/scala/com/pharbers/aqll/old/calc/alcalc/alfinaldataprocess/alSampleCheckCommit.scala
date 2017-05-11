package com.pharbers.aqll.old.calc.alcalc.alfinaldataprocess

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.old.calc.util.dao._data_connection_cores
/**
  * Created by liwei on 2017/4/18.
  */
object alSampleCheckCommit {
  def apply(company: String): alSampleCheckCommit = new alSampleCheckCommit(company)
}

class alSampleCheckCommit(company: String){
  try {
    val lst_temp = _data_connection_cores.getCollection("FactResult").find(new MongoDBObject(MongoDBObject("Company" -> company)))
    while(lst_temp.hasNext) {
      val c : DBObject = lst_temp.next()
      _data_connection_cores.getCollection("SampleCheckResult").findAndRemove(
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
      _data_connection_cores.getCollection("SampleCheckResult").insert(
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
      _data_connection_cores.getCollection("FactResult").findAndRemove(new MongoDBObject(MongoDBObject("Company" -> c.get("Company"))))
    }
  } catch {
    case e: Exception => println(e.getMessage)
  }
}