package com.pharbers.aqll.alCalcOther.alfinaldataprocess

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.alCalaHelp.dbcores._
import com.pharbers.aqll.common.alCmd.dbcmd.dbrestoreCmd2
import com.pharbers.aqll.common.alFileHandler.databaseConfig._

/**
  * Created by liwei on 2017/3/20.
  */

case class alRestoreColl2() {

    def apply(company : String, sub_uuids : List[String]) = {
        var isfirst : Boolean = false
        sub_uuids foreach{ x =>
//            dbrestoreCmd(db1, company, s"$root$program$scpPath$x", dbuser, dbpwd, dbhost, dbport.toInt).excute
            dbrestoreCmd2(db1, company, "config/dumpdb/Max_Cores/" + x, dbuser, dbpwd, dbhost, dbport.toInt).excute
//            dbrestoreCmd(db1, company, s"$root$program$dumpdb/Max_Cores/" + x, dbuser, dbpwd, dbhost, dbport.toInt).excute
            if(!isfirst){
                dbc.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("City" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Market" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1,"City" -> 1))
            }
            isfirst = true
        }
    }
}