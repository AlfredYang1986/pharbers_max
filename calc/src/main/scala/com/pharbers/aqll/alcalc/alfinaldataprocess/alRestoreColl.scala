package com.pharbers.aqll.alcalc.alfinaldataprocess

import com.pharbers.aqll.alcalc.alcmd.dbcmd._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.util.dao._data_connection
/**
  * Created by LIWEI on 2017/3/20.
  */
object alRestoreColl {
    def apply(company : String, sub_uuids : List[String]): alRestoreColl = new alRestoreColl(company, sub_uuids)
}

class alRestoreColl(company : String, sub_uuids : List[String]){
    var isfirst : Boolean = false
    sub_uuids foreach{ x =>
        dbrestoreCmd("Max_Cores",company+"_temp",x).excute
        if(!isfirst){_data_connection.getCollection(company+"_temp").createIndex(MongoDBObject("Index" -> 1))}
        isfirst = true
    }
}
