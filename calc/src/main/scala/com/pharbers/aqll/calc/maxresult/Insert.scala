package com.pharbers.aqll.calc.maxresult

import com.pharbers.aqll.calc.util.MD5
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.util.dao._data_connection
import java.util.Date

object Insert { 
     def maxResultInsert(mr: Stream[(Long, (Double, Double))])(m: (String, String, String, Long)) = {
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
}