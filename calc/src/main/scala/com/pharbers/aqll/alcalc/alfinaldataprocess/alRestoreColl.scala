package com.pharbers.aqll.alcalc.alfinaldataprocess

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.common.alCmd.dbcmd.dbrestoreCmd
import com.pharbers.aqll.common.alDao._data_connection_cores
import com.pharbers.aqll.util.fileConfig._
/**
  * Created by LIWEI on 2017/3/20.
  */

object alRestoreColl {
    def apply(company : String, sub_uuids : List[String]): alRestoreColl = new alRestoreColl(company, sub_uuids)
}

class alRestoreColl(company : String, sub_uuids : List[String]){
    var isfirst : Boolean = false
    sub_uuids foreach{ x =>
//        dbrestoreCmd("Max_Cores",company+"_temp",x).excute
        dbrestoreCmd("Max_Cores", company, scpPath + x, "Pharbers", "Pharbers2017.").excute
        if(!isfirst){_data_connection_cores.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))}
        isfirst = true
    }
}
