package com.pharbers.aqll.calc.util

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.util.dao._data_connection
import java.util.Date
import com.pharbers.aqll.calc.util.dao.from

object test extends App{
    val count = (from db() in "MaxResults" where ("MaxResults_Id" -> "75e33dc21ac7aa678b00f94881dc4e9b") count)
    println(count)
    val query1 =MongoDBObject("MaxResults_Id"->"75e33dc21ac7aa678b00f94881dc4e9b")
    println(query1)
    _data_connection.getCollection("MaxResults").remove(query1)
}