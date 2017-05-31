package com.pharbers.aqll.alCalcOther.alfinaldataprocess.scala

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.alCalaHelp.DBList
import com.pharbers.aqll.common.alFileHandler.databaseConfig._
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alCmd.dbcmd.dbrestoreCmd

/**
  * Created by liwei on 2017/3/20.
  */

case class alRestoreColl() extends DBList{
    implicit val dbc = dbcores

    def apply(company : String, sub_uuids : List[String]) = {
        var isfirst : Boolean = false
        sub_uuids foreach{ x =>
            dbrestoreCmd(db1, company, root + scpPath + x, dbuser, dbpwd, dbhost, dbport.toInt).excute
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