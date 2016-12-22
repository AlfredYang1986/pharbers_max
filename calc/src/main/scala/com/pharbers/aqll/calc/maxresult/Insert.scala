package com.pharbers.aqll.calc.maxresult

import com.pharbers.aqll.calc.util.MD5
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.util.dao._data_connection
import java.util.Date
import com.pharbers.aqll.calc.util.dao.from
import scala.collection.mutable.ArrayBuffer

object Insert {
    
    def maxResultInsert(mr: List[(String, (Long, Double, Double, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], String))]) (m: (String, String, String, Long)) = {
        def maxInser() = {
            mr.toList map { x =>
                 val builder = MongoDBObject.newBuilder
                 builder += "ID" -> m._3
                 builder += "Units" -> x._2._3
                 builder += "Sales" -> x._2._2
                 builder += "Condition" -> Map("Hospital" -> x._2._4 , "ProductMinunt" -> x._2._5, "Market" -> x._2._6)
                 builder += "Timestamp" -> x._2._1
                 builder += "Createtime" -> m._4
                 builder += "Filepath" -> m._1
                 builder += "Rtype" -> x._2._7
                 _data_connection.getCollection("FinalResult") += builder.result
             }
        }
         println(s"mr.toList.size = ${mr.toList.size}")
         println(s"mr.toList.map(_._2._1).sum = ${mr.toList.map(_._2._2).sum}")
         println(s"mr.toList.map(_._2._2).sum = ${mr.toList.map(_._2._3).sum}")
         
         val conditions = ("ID" -> m._3)
         val count = (from db() in "FinalResult" where conditions count)
         println(s"count = ${count}")
         count match {
             case 0 => {
                 maxInser()
             }
             
             case _ => {
                 val rm =MongoDBObject("MaxResults_Id"-> m._3)
                 _data_connection.getCollection("MaxResults").remove(rm)
                 maxInser()
             }
         }
     }
}