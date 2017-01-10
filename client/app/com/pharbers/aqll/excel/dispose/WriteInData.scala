package com.pharbers.aqll.excel.dispose

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao._data_connection_cores
import com.pharbers.aqll.excel.dispose.queryCoresData._
import com.pharbers.aqll.excel.dispose.ReadFileData._

object WriteInData{
    def insertHospitalCoresInfo(excel_file_name : String){
        val hospitalinfobulk = getbulk(getMongoCollection("HospitalInfo"))
        val hospdatadata = hospitaldata(excel_file_name)
        println(s"size=${hospdatadata.size}")
        hospitalInfo(hospdatadata).foreach(x => hospitalinfobulk.insert(x))
        getMongoCollection("HospitalInfo").drop()
        hospitalinfobulk.execute()
    }
    def insertProductsCoresInfo(excel_file_name : String){
        val productsinfobulk = getbulk(getMongoCollection("MinimumProductInfo"))
        val productdata = productmatchdata(excel_file_name)
        println(s"size=${productdata.size}")
        minimumProductInfo(productdata).foreach(x => productsinfobulk.insert(x))
        getMongoCollection("MinimumProductInfo").drop()
        productsinfobulk.execute()
    }

    def getMongoCollection(coll_name : String) : MongoCollection = {
        _data_connection_cores.getCollection(coll_name)
    }
    
    def getbulk(mongo_collection : MongoCollection) : BulkWriteOperation = {
        mongo_collection.initializeUnorderedBulkOperation
    }
}
