package com.pharbers.aqll.alCalcHelp.alFinalDataProcess

import java.io.File
import com.pharbers.aqll.alCalcHelp.dbcores._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.alCalcHelp.alLog.alTempLog
import com.pharbers.aqll.common.alCmd.dbcmd.dbrestoreCmd3
import com.pharbers.aqll.common.alFileHandler.databaseConfig._

/**
  * Created by jeorch on 2017/11/14.
  *     Modify by clock on 2017.12.21
  */
case class alRestoreColl() {
    def apply(company: String, bsonpath: String) = {
        var first: Boolean = false
        var bson_path = alBsonPath().bson_file_path
        val file = new File(bson_path + bsonpath)
        val fileList = file.listFiles()
        alTempLog(s"restore bson files count = ${fileList.length}")

        fileList.foreach(x => {
            dbrestoreCmd3(db1, company, x.toString, dbuser, dbpwd, dbhost, dbport.toInt).excute
            if(first){
                dbc.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("City" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Market" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1,"City" -> 1))
            }
            first = true
        })
    }
}