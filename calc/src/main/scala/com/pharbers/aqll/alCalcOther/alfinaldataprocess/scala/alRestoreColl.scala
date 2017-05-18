package com.pharbers.aqll.alCalcOther.alfinaldataprocess.scala

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.common.alFileHandler.databaseConfig._
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alCmd.dbcmd.dbrestoreCmd
import com.pharbers.aqll.common.alDao._data_connection_cores

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
        dbrestoreCmd(db1, company, scpPath + x, dbuser, dbpwd, dbhost, dbport.toInt).excute
        if(!isfirst){_data_connection_cores.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))}
        isfirst = true
    }
}
