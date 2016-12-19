package com.pharbers.aqll.excel.dispose

import scala.math._
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.excel.common._
import com.pharbers.aqll.excel.dispose.AssemblyBasicData._
import com.pharbers.aqll.excel.dispose.AssemblyCoresData._
import com.pharbers.aqll.excel.dispose.CroesMatchData._
import com.pharbers.aqll.excel.dispose.BasicMatchData._
import com.pharbers.aqll.excel.model._
import com.pharbers.aqll.util.MD5
import com.pharbers.aqll.util.dao._
import com.pharbers.aqll.excel.common.ReadFileData._
import com.pharbers.aqll.util.errorcode.ErrorCode._
import com.pharbers.aqll.excel.exception._
import com.pharbers.aqll.util.errorcode.ErrorCode

object WriteInBasicData{
    def insertHospitalBasicInfo(excel_file_name : String) {
        
        val hospitalbulk = getbulk(getMongoCollection("Hospital"))
        val provincebulk = getbulk(getMongoCollection("Province"))
        val citybulk = getbulk(getMongoCollection("City"))
        val specialtybulk = getbulk(getMongoCollection("Specialty"))
        val universityHospbulk = getbulk(getMongoCollection("UniversityHospInfo"))
        
        try {
            val hospdatadata = hospdatadataobj(excel_file_name)
            hospdatadata.size match {
                case 0 => throw new ExcelDataException(ErrorCode.getErrorMessageByName("data is null"))
                case _ => {
                    hospitalData(hospdatadata) foreach { x => hospitalbulk.insert(x)}
                    provinceData(hospdatadata) foreach { x => provincebulk.insert(x)}
                    cityData(hospdatadata) foreach { x => citybulk.insert(x)}
                    specialtyData(hospdatadata) foreach { x=> specialtybulk.insert(x)}
                    universityHospInfoData(hospdatadata) foreach { x => universityHospbulk.insert(x)}
                    
                    getMongoCollection("Hospital").drop()
                    getMongoCollection("Province").drop()
                    getMongoCollection("City").drop()
                    getMongoCollection("Specialty").drop()
                    getMongoCollection("UniversityHospInfo").drop()
                    
                    hospitalbulk.execute()
                    provincebulk.execute()
                    citybulk.execute()
                    specialtybulk.execute()
                    universityHospbulk.execute()
                }
            }
            
        } catch {
          case ReadFileException(m) => throw new ExcelDataException(ErrorCode.getErrorMessageByName("data is null"))
        }
    }
    def insertProductBasicInfo(excel_file_name : String) {
        val productsbulk = getbulk(getMongoCollection("Products"))
        val dosageFormsbulk = getbulk(getMongoCollection("DosageForms"))
        val drugspecificationbulk = getbulk(getMongoCollection("DrugSpecifications"))
        val manufacturerbulk = getbulk(getMongoCollection("Manufacturer"))
        
        try {
          val productdata = productdataobj(excel_file_name)
          productdata.size match {
              case 0 => throw new ExcelDataException(ErrorCode.getErrorMessageByName("data is null"))
              case _ => {
                  productsData(productdata) foreach { x =>productsbulk.insert(x)}
                  dosageFormsData(productdata) foreach { x => dosageFormsbulk.insert(x)}
                  drugspecificationData(productdata) foreach { x => drugspecificationbulk.insert(x)}
                  manufacturerData(productdata) foreach { x => manufacturerbulk.insert(x)}
                
                  getMongoCollection("Products").drop()
                  getMongoCollection("DosageForms").drop()
                  getMongoCollection("DrugSpecifications").drop()
                  getMongoCollection("Manufacturer").drop()
                
                  productsbulk.execute()
                  dosageFormsbulk.execute()
                  drugspecificationbulk.execute()
                  manufacturerbulk.execute()
              }
          }
        } catch {
          case ReadFileException(m) => throw new ExcelDataException(ErrorCode.getErrorMessageByName("data is null"))
        }
    }
    
    def getMongoCollection(coll_name : String) : MongoCollection = {
        conn_define.conn_basic.getCollection(coll_name)
    }
    
    def getbulk(mongo_collection : MongoCollection) : BulkWriteOperation = {
        mongo_collection.initializeUnorderedBulkOperation
    }
}
object WriteInCoresData{
    def insertHospitalCoresInfo(excel_file_name : String){
        val hospitalinfobulk = getbulk(getMongoCollection("HospitalInfo"))
        val regioninfobulk = getbulk(getMongoCollection("Region"))
         try {
          val hospdatadata = hospdatadataobj(excel_file_name)
          hospdatadata.size match {
              case 0 => throw new ExcelDataException(ErrorCode.getErrorMessageByName("data is null"))
              case _ => {
                  hospitalInfo(hospdatadata).foreach(x => hospitalinfobulk.insert(x))
                  regionData(hospdatadata).foreach(x => regioninfobulk.insert(x))
                  getMongoCollection("HospitalInfo").drop()
                  getMongoCollection("Region").drop()
                  hospitalinfobulk.execute()
                  regioninfobulk.execute()
              }
          }
        } catch {
          case ReadFileException(m) => throw new ExcelDataException(ErrorCode.getErrorMessageByName("data is null"))
        }
    }
    def insertProductsCoresInfo(excel_file_name : String){
        val productsinfobulk = getbulk(getMongoCollection("MinimumProductInfo"))
        try {
          val productdata = productdataobj(excel_file_name)
          productdata.size match {
              case 0 => throw new ExcelDataException(ErrorCode.getErrorMessageByName("data is null"))
              case _ => {
                  minimumProductInfo(productdata).foreach(x => productsinfobulk.insert(x))
                  getMongoCollection("MinimumProductInfo").drop()
                  productsinfobulk.execute()
              }
          }
        } catch {
          case ReadFileException(m) => throw new ExcelDataException(ErrorCode.getErrorMessageByName("data is null"))
        }
    }
    
    def insertCoresInfo(excel_file_name : String){
        val segmentinfobulk = getbulk(getMongoCollection("SegmentDefinition"))
        val segmentdata = segmentdataobj(excel_file_name)
        segmentMatch(segmentdata).foreach(x => segmentinfobulk.insert(x))
        getMongoCollection("SegmentDefinition").drop()
        segmentinfobulk.execute()
    }
    
    def getMongoCollection(coll_name : String) : MongoCollection = {
        conn_define.conn_cores.getCollection(coll_name)
    }
    
    def getbulk(mongo_collection : MongoCollection) : BulkWriteOperation = {
        mongo_collection.initializeUnorderedBulkOperation
    }
}

object BasicWriteInData{
    def insertAtcCodeBasicInfo(excel_file_name : String){
        val bulk = getbulk(getMongoCollection("AtcCode"))
        val data = atccodedataobj(excel_file_name)
        getMongoCollection("AtcCode").drop()
        atccodeMatch(data).foreach(x => bulk.insert(x))
        bulk.execute()
    }
    
    def insertDrugBasicInfo(excel_file_name : String){
        val bulk = getbulk(getMongoCollection("Drugs"))
        val data = durgdataobj(excel_file_name)
        getMongoCollection("Drugs").drop()
        durgMatch(data).foreach(x => bulk.insert(x))
        bulk.execute()
    }
    
    def insertRouteofMedicationBasicInfo(excel_file_name : String){
        val bulk = getbulk(getMongoCollection("RouteofMedication"))
        val data = routeofmedicationdataobj(excel_file_name)
        getMongoCollection("RouteofMedication").drop()
        routeofmedicationMatch(data).foreach(x => bulk.insert(x))
        bulk.execute()
    }
    
    def insertSegmentBasicInfo(excel_file_name : String){
        val bulk = getbulk(getMongoCollection("Segment"))
        val data = segmentbasicdataobj(excel_file_name)
        getMongoCollection("Segment").drop()
        segmentbasicMatch(data).foreach(x => bulk.insert(x))
        bulk.execute()
    }
    
    def insertDatasourceBasicInfo(){
        val bulk = getbulk(getMongoCollection("DataSources"))
        getMongoCollection("DataSources").drop()
        datasourceMatch().foreach(x => bulk.insert(x))
        bulk.execute()
    }
    
    def insertCompanyBasicInfo(){
        val bulk = getbulk(getMongoCollection("Company"))
        getMongoCollection("Company").drop()
        companyMatch().foreach(x => bulk.insert(x))
        bulk.execute()
    }
    
    def insertMarketBasicInfo(){
        val bulk = getbulk(getMongoCollection("Market"))
        getMongoCollection("Market").drop()
        marketMatch().foreach(x => bulk.insert(x))
        bulk.execute()
    }
    
    def getMongoCollection(coll_name : String) : MongoCollection = {
        conn_define.conn_basic.getCollection(coll_name)
    }
    
    def getbulk(mongo_collection : MongoCollection) : BulkWriteOperation = {
        mongo_collection.initializeUnorderedBulkOperation
    }
}
