package com.pharbers.aqll.excel.dispose

import scala.math._
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.excel.common._
import com.pharbers.aqll.excel.dispose.splitData._
import com.pharbers.aqll.excel.model._
import com.pharbers.aqll.util.MD5
import com.pharbers.aqll.util.dao._
import com.pharbers.aqll.excel.common.ReadFileData._

object joinData{
    /**
     * inputHospitalInfo function
     * Hospital
     * Province
     * City
     * Specialty
     * UniversityHospInfo
     */
    def inputHospitalData(excel_file_name : String, xml_file_name : String, xml_file_name_ch : String){
        
        val hospitalbulk = _data_connection.getCollection("Hospital").initializeUnorderedBulkOperation
        val provincebulk = _data_connection.getCollection("Province").initializeUnorderedBulkOperation
        val citybulk = _data_connection.getCollection("City").initializeUnorderedBulkOperation
        val specialtybulk = _data_connection.getCollection("Specialty").initializeUnorderedBulkOperation
        val universityHospbulk = _data_connection.getCollection("UniversityHospInfo").initializeUnorderedBulkOperation
        val hospdatadata : List[Hospital] = hospdatadataobj(excel_file_name, xml_file_name, xml_file_name_ch)
        
        val result : Boolean = try{
            hospitalData(hospdatadata) foreach { x =>
                hospitalbulk.insert(Map("Hosp_Id" -> MD5.md5(x(0)+x(1)),"Pha_Code" -> x(1),"Hosp_Name" -> x(0),"Tag" -> (
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
                    })
                 )))
            }
            
            provinceData(hospdatadata).distinct foreach { x => 
                 provincebulk.insert(Map("Province_Id" -> MD5.md5(x(0)), "Province_Name" -> x(0)))
            }
            
            cityData(hospdatadata).distinct foreach { x => 
                citybulk.insert(Map("City_Id" -> MD5.md5(x(0).asInstanceOf[String]+ x(1).asInstanceOf[String]),"City_Name" -> x(0),"City_Tier" -> (x(1) match {
                    case "1" => pow(2,0).asInstanceOf[Int]
                    case "2" => pow(2,1).asInstanceOf[Int]
                    case "3" => pow(2,2).asInstanceOf[Int]
                    case "4" => pow(2,3).asInstanceOf[Int]
                    case "5" => pow(2,4).asInstanceOf[Int]
                })))
            }
            
            specialtyData(hospdatadata).distinct foreach { x=>
                specialtybulk.insert(Map("Specialty_Id" -> MD5.md5(x(0)),"Specialty_Classification" -> x(0)))
            }
            
            universityHospInfoData(hospdatadata) foreach { x => 
                universityHospbulk.insert(Map(
                        "Hosp_Id" -> MD5.md5(x(0)+x(1).toString()),
                        "bedsnum" -> x(2),
                        "bedsnum_general_medicine" -> x(3),
                        "bedsnum_internal_medicine" -> x(4),
                        "bedsnum_surgery_department" -> x(5),
                        "bedsnum_ophthalmology" -> x(6),
                        "diagnosis_ppl_annual" -> x(7),
                        "diagnosis_amount_clinic" -> x(8),
                        "diagnosis_amount_internalmedicine" -> x(9),
                        "diagnosis_amount_surgerydepartment" -> x(10),
                        "inpatient_ppl_annual" -> x(11),
                        "inpatient_operation_amount_annual" -> x(12),
                        "income_diagnosis_treatment" -> x(13),
                        "income_clinic" -> x(14),
                        "income_clinic_diagnosis_treatment" -> x(15),
                        "income_clinic_operation" -> x(16),
                        "income_inpatient" -> x(17),
                        "income_inpatient_beds" -> x(18),
                        "income_inpatient_diagnosis_treatment" -> x(19),
                        "income_inpatient_operation" -> x(20),
                        "income_drugs" -> x(21),
                        "income_clinic_drugs" -> x(22),
                        "income_clinic_wst_drugs" -> x(23),
                        "income_inpatient_drugs" -> x(24),
                        "income_inpatient_wst_drugs" -> x(25)
                ))
            }
            true
        }catch{
            case ex : Exception => Unit
            false
        }
        if(result){
            //暂时还没找到批量清空数据的方法，等找到之后可以替换这段代码
            _data_connection.getCollection("Hospital").drop()
            _data_connection.getCollection("Province").drop()
            _data_connection.getCollection("City").drop()
            _data_connection.getCollection("Specialty").drop()
            _data_connection.getCollection("UniversityHospInfo").drop()
            
            hospitalbulk.execute()
            provincebulk.execute()
            citybulk.execute()
            specialtybulk.execute
            universityHospbulk.execute()
        }
    }
    /**
     * inputProductInfo function
     * Products
     * DosageForms
     * DrugSpecifications
     * Manufacturer
     */
    def inputProductInfo(excel_file_name : String, xml_file_name : String, xml_file_name_ch : String) {
        
        val productsbulk = _data_connection.getCollection("Products").initializeUnorderedBulkOperation
        val dosageFormsbulk = _data_connection.getCollection("DosageForms").initializeUnorderedBulkOperation
        val drugspecificationbulk = _data_connection.getCollection("DrugSpecifications").initializeUnorderedBulkOperation
        val manufacturerbulk = _data_connection.getCollection("Manufacturer").initializeUnorderedBulkOperation
        val productdata : List[Products] = productdataobj(excel_file_name, xml_file_name, xml_file_name_ch)
        val result : Boolean = try{
            productsData(productdata).groupBy(x => x(0)).map( y => Map(y._1 -> y._2.map(z => z(1)).distinct)).toList foreach { v =>
                productsbulk.insert(Map("Prod_Id" -> MD5.md5(v.keys.head.toString), "Trade_Name" -> (Map("Ch" -> v.keys.head,"En" -> "") :: Nil), "Package_Quantity" -> v.values.head))
            }
            
            dosageFormsData(productdata).distinct foreach { x =>
                dosageFormsbulk.insert(Map("Dosageform_Id" -> MD5.md5(x(0)), "Dosageform" -> (Map("Ch" -> x(0),"En" -> "") :: Nil)))
            }
            
            drugspecificationData(productdata).distinct foreach { x =>
                drugspecificationbulk.insert(Map("Drugspecification_Id" -> MD5.md5(x(0)), "Drugspecification" -> (Map("Ch" -> "","En" -> x(0)) :: Nil)))
            }
            
            manufacturerData(productdata).distinct foreach { x =>
                manufacturerbulk.insert(Map("Manufacturer_Id" -> MD5.md5(x(0)), "Manufacturer_Name" -> (Map("Ch" -> x(0),"En" -> "") :: Nil)))
            }
            true
        }catch{
            case ex : Exception => Unit
            false
        }
        if(result){
            //暂时还没找到批量清空数据的方法，等找到之后可以替换这段代码
            _data_connection.getCollection("Products").drop()
            _data_connection.getCollection("DosageForms").drop()
            _data_connection.getCollection("DrugSpecifications").drop()
            _data_connection.getCollection("Manufacturer").drop()
            productsbulk.execute()
            dosageFormsbulk.execute()
            drugspecificationbulk.execute()
            manufacturerbulk.execute()
        }
    }
    
}
