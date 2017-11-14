package com.pharbers.aqll.alCalcOther.alfinaldataprocess

import java.io.File

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.alCalaHelp.dbcores._
import com.pharbers.aqll.common.alCmd.dbcmd.{dbrestoreCmd2, dbrestoreCmd3}
import com.pharbers.aqll.common.alFileHandler.databaseConfig._

/**
  * Created by jeorch on 2017/11/14.
  */

case class alRestoreColl3() {

    def apply(company : String, sub_uuids : List[String]) = {
        var isfirst : Boolean = false

        val bson_path = "config/dumpdb/Max_Cores"
        val file: File = new File(bson_path)
        val fileList: Array[File] = file.listFiles()
        println(s"=== files count = ${fileList.length} ===")
        fileList.foreach(x => {
            dbrestoreCmd3(db1, company, x.toString, dbuser, dbpwd, dbhost, dbport.toInt).excute
            if(!isfirst){
                dbc.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("City" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Market" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1))
                dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1,"City" -> 1))
            }
            isfirst = true
        })

    }
}