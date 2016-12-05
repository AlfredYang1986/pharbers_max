package com.pharbers.aqll.calc.maxresult

import com.pharbers.aqll.calc.util.MD5
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.util.dao._data_connection
import java.util.Date
import com.pharbers.aqll.calc.util.dao.from

object Insert { 
     def maxResultInsert(mr: Stream[(Long, (Double, Double))])(m: (String, String, String, Long)) = {
         val conditions = ("MaxResults_Id" -> m._3)
         val count = (from db() in "MaxResults" where conditions count)
         count match {
             case 0 => {
                     mr.toList map { x =>
                     val builder = MongoDBObject.newBuilder
                     builder += "MaxResults_Id" -> m._3
                     builder += "FileName" -> m._1
                     builder += "Company" -> m._2
                     builder += "Sales" -> x._2._1
                     builder += "Units" -> x._2._2
                     builder += "Sales_Date" -> x._1
                     builder += "Creation_Date" -> m._4
                     _data_connection.getCollection("MaxResults") += builder.result
                 }
             }
             
             case _ => {
                 val rm =MongoDBObject("MaxResults_Id"-> m._3)
                 _data_connection.getCollection("MaxResults").remove(rm)
             }
         }
     }
}