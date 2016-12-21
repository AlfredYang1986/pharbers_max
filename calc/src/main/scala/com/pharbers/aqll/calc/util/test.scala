package com.pharbers.aqll.calc.util

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.util.dao._data_connection
import java.util.Date
import com.pharbers.aqll.calc.util.dao.from
import scala.concurrent.stm.Ref
import scala.concurrent.stm.atomic
//import scala.collection.mutable.Map

object test extends App{
//    val count = (from db() in "MaxResults" where ("MaxResults_Id" -> "75e33dc21ac7aa678b00f94881dc4e9b") count)
//    println(count)
//    val query1 =MongoDBObject("MaxResults_Id"->"75e33dc21ac7aa678b00f94881dc4e9b")
//    println(query1)
//    _data_connection.getCollection("MaxResults").remove(query1)
    
//    val aa = Map("aa" -> "aaa")
//    aa.put("bb", "bbb")
//    aa.put("aa", "bbb")
//    println(aa)
    
//    val mrResult = Ref(Map[Long, (Double, Double)]())
//    for(i <- 1 to 10){
//        atomic { implicit thx => 
//			mrResult() = mrResult() + (1.asInstanceOf[Number].longValue() -> (1.0, 2.0))
//        }
//    }
//    
//    println(mrResult.single.get)
    
//    DateUtil.getDateLong(2015,2)
    
    println(DateUtil.getIntegralStartTime(new Date()).getTime)
}