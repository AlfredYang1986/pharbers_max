package com.pharbers.aqll.calc.maxresult

import com.pharbers.aqll.calc.util.MD5
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.util.dao._data_connection
import java.util.Date
import com.pharbers.aqll.calc.util.dao.from
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.datacala.common.CommonArg
import com.mongodb.casbah.commons.MongoDBList

object Insert {
    
    def maxResultInsert(mr: List[(String, (Long, Double, Double, ArrayBuffer[(String)], ArrayBuffer[(String)], ArrayBuffer[(String)], String))]) (m: (String, String, String, Long)) = {
        def maxInser() = {
            mr.toList map { x =>
                 val builder = MongoDBObject.newBuilder
                 builder += "ID" -> m._3
                 builder += "Units" -> x._2._3
                 builder += "Sales" -> x._2._2
                 builder += "Hospital" -> x._2._4.head
                 builder += "ProductMinunt" -> x._2._5.head
                 builder += "Market" -> x._2._6.head
//                 builder += "Condition" -> Map("Hospital" -> x._2._4.head , "ProductMinunt" -> x._2._5.head, "Market" -> x._2._6.head)
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
                 val rm =MongoDBObject(conditions)
                 _data_connection.getCollection("FinalResult").remove(rm)
                 maxInser()
             }
         }
     }
    
    def maxFactResultInsert(model:  (Double, Double, Int, List[String], List[String]))(m: (String, String, String, Long)) = {
        def maxInser() = {
            val builder = MongoDBObject.newBuilder
            builder += "ID" -> m._3
            builder += "Units" -> model._2
            builder += "Sales" -> model._1
            builder += "HospitalNum" -> model._3
            builder += "ProductMinuntNum" -> model._5.size
            val lsth_builder = MongoDBList.newBuilder
            model._4 foreach (lsth_builder += _)
            val lstm_builder = MongoDBList.newBuilder
            model._5 foreach (lstm_builder += _)
            
            builder += "Condition" -> Map("Hospital" -> lsth_builder.result, "ProductMinunt" -> lstm_builder.result)
            builder += "Timestamp" -> m._4
            builder += "Filepath" -> m._1
            _data_connection.getCollection("FactResult") += builder.result
        }
        val conditions = ("ID" -> m._3)
        val count = (from db() in "FactResult" where conditions count)
        count match {
             case 0 => {
                 maxInser()
             }
             
             case _ => {
                 val rm =MongoDBObject(conditions)
                 _data_connection.getCollection("FactResult").remove(rm)
                 maxInser()
             }
         }
    }
}