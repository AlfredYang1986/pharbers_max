package com.pharbers.aqll.excel.dispose

import com.pharbers.aqll.excel.dispose.splitData._
import com.pharbers.aqll.excel.common.ReadFileData._
import com.pharbers.aqll.excel.model._
import com.pharbers.aqll.util.dao._
import com.mongodb.casbah.Imports._
import scala.math._
import com.pharbers.aqll.util.errorcode.ErrorCode._
import com.pharbers.aqll.util.MD5

object joinData {
    
    val hospitalFileName = """file/excel/医院大全.xlsx"""
    val productsFileName = """file/excel/产品大全.xlsx"""
    
    //医院
    def joinHospital = {
        hospitalData(hospdatadataobj(hospitalFileName)) foreach { x => 
            val builder = MongoDBObject.newBuilder
            builder += "Hosp_Id" -> MD5.md5(x(0)+x(1))
            builder += "Pha_Code" -> x(1)
            builder += "Hosp_Name" -> x(0)
            builder += "Tag" -> (
            (x(2) match{
                case "一级" => pow(2,2).asInstanceOf[Int]
                case "二级" => pow(2,3).asInstanceOf[Int]
                case "三级" => pow(2,4).asInstanceOf[Int]
            })+
            (x(3) match {
                case "综合" => pow(2,5).asInstanceOf[Int]
                case "专科" => pow(2,6).asInstanceOf[Int]
            })+
            (x(4) match{
                case "是" => pow(2,0).asInstanceOf[Int]
                case "否" => pow(2,1).asInstanceOf[Int]
            }))
            _data_connection.getCollection("Hospital") += builder.result
        }
    }
    //省份
    def joinProvince = {
        provinceData(hospdatadataobj(hospitalFileName)).distinct foreach { x => 
            val builder = MongoDBObject.newBuilder
            builder += "Province_Id" -> MD5.md5(x(0))
            builder += "Province_Name" -> x(0)
            _data_connection.getCollection("Province") += builder.result
        }
    }
    //城市
    def joinCity = {
        cityData(hospdatadataobj(hospitalFileName)).distinct foreach { x => 
            val builder = MongoDBObject.newBuilder  
            builder += "City_Id" -> MD5.md5(x(0).asInstanceOf[String]+ x(1).asInstanceOf[String])
            builder += "City_Name" -> x(0)
            builder += "City_Tier" -> (x(1).toString match {
                case "1" => pow(2,0).asInstanceOf[Int]
                case "2" => pow(2,1).asInstanceOf[Int]
                case "3" => pow(2,2).asInstanceOf[Int]
                case "4" => pow(2,3).asInstanceOf[Int]
                case "5" => pow(2,4).asInstanceOf[Int]
            })
            _data_connection.getCollection("City") += builder.result
        }
    }
    //特殊专科
    def joinSpecialty = {
        specialtyData(hospdatadataobj(hospitalFileName)).distinct foreach { x=>
            val builder = MongoDBObject.newBuilder
            builder += "Specialty_Id" -> MD5.md5(x(0))
            builder += "Specialty_Classification" -> x(0)
            _data_connection.getCollection("Specialty") += builder.result
        }
    }
    //计算变量
    def joinUniversityHospInfo = {
        universityHospInfoData(hospdatadataobj(hospitalFileName)) foreach { x => 
            val builder = MongoDBObject.newBuilder  
            builder += "Hosp_Id" -> MD5.md5(x(0)+x(1).toString())
            builder += "bedsnum" -> x(2)
            builder += "bedsnum_general_medicine" -> x(3)
        	builder += "bedsnum_internal_medicine" -> x(4)
        	builder += "bedsnum_surgery_department" -> x(5)
        	builder += "bedsnum_ophthalmology" -> x(6)
        	builder += "diagnosis_ppl_annual" -> x(7)
        	builder += "diagnosis_amount_clinic" -> x(8)
        	builder += "diagnosis_amount_internalmedicine" -> x(9)
        	builder += "diagnosis_amount_surgerydepartment" -> x(10)
        	builder += "inpatient_ppl_annual" -> x(11)
        	builder += "inpatient_operation_amount_annual" -> x(12)
        	builder += "income_diagnosis_treatment" -> x(13)
        	builder += "income_clinic" -> x(14)
        	builder += "income_clinic_diagnosis_treatment" -> x(15)
        	builder += "income_clinic_operation" -> x(16)
        	builder += "income_inpatient" -> x(17)
        	builder += "income_inpatient_beds" -> x(18)
        	builder += "income_inpatient_diagnosis_treatment" -> x(19)
        	builder += "income_inpatient_operation" -> x(20)
        	builder += "income_drugs" -> x(21)
        	builder += "income_clinic_drugs" -> x(22)
        	builder += "income_clinic_wst_drugs" -> x(23)
        	builder += "income_inpatient_drugs" -> x(24)
        	builder += "income_inpatient_wst_drugs" -> x(25)
        	_data_connection.getCollection("UniversityHospInfo") += builder.result
    	}
    }
    //商品
    def joinProducts = {
        productsData(productdataobj(productsFileName)).groupBy(x => x(0)).map( y => Map(y._1 -> y._2.map(z => z(1)).distinct)).toList foreach { v =>
            val builder = MongoDBObject.newBuilder
            builder += "Prod_Id" -> MD5.md5(v.keys.head.toString)
            builder += "Trade_Name" -> (Map("Ch" -> v.keys.head,"En" -> "") :: Nil)
            builder += "Package_Quantity" -> v.values.head
//            List(500)
//            .::: (List(30, 32, 35, 36, 40, 42, 45, 48, 50, 56, 60, 64, 72, 80, 96, 100, 112, 120, 150, 300, 400)) 
//            .::: (List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 22, 24, 25, 28))
            _data_connection.getCollection("Products") += builder.result
         }
    }
    //剂型
    def joinDosageForms = {
        dosageFormsData(productdataobj(productsFileName)).distinct foreach { x =>
            val builder = MongoDBObject.newBuilder
            builder += "Dosageform_Id" -> MD5.md5(x(0))
            builder += "Dosageform" -> (Map("Ch" -> x(0),"En" -> "") :: Nil)
            _data_connection.getCollection("DosageForms") += builder.result
        }
    }
    //规格
    def joinDrugspecification = {
        drugspecificationData(productdataobj(productsFileName)).distinct foreach { x =>
            val builder = MongoDBObject.newBuilder
            builder += "Drugspecification_Id" -> MD5.md5(x(0))
            builder += "Drugspecification" -> (Map("Ch" -> x(0),"En" -> "") :: Nil)
            _data_connection.getCollection("DrugSpecifications") += builder.result
        }
    }
    //生产企业
    def joinManufacturer = {
        manufacturerData(productdataobj(productsFileName)).distinct foreach { x =>
            val builder = MongoDBObject.newBuilder
            builder += "Manufacturer_Id" -> MD5.md5(x(0))
            builder += "Manufacturer_Name" -> (Map("Ch" -> x(0),"En" -> "") :: Nil)
            _data_connection.getCollection("Manufacturer") += builder.result
        }
    }
}