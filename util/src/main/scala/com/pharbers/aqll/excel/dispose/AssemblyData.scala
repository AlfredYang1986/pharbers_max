package com.pharbers.aqll.excel.dispose

import com.pharbers.aqll.excel.model._
import scala.math._
import com.pharbers.aqll.util._

object AssemblyBasicData{
    def hospitalData(data: List[Hospital]): List[Map[String, AnyRef]] = {
        data map { x => (
           Map("Hosp_Id" -> MD5.md5(x.getHosp_Name+x.getPha_Code),
               "Hosp_Name" -> x.getHosp_Name, 
               "Pha_Code" -> x.getPha_Code, 
               "Tag" -> (
                    (x.getHosp_level match{
                        case "一级" => pow(2,2).asInstanceOf[Int]
                        case "二级" => pow(2,3).asInstanceOf[Int]
                        case "三级" => pow(2,4).asInstanceOf[Int]
                    })+
                    (x.getSpecialty match {
                        case "综合" => pow(2,5).asInstanceOf[Int]
                        case "专科" => pow(2,6).asInstanceOf[Int]
                    })+
                    (x.getIf_County match{
                        case "是" => pow(2,0).asInstanceOf[Int]
                        case "否" => pow(2,1).asInstanceOf[Int]
                    })).asInstanceOf[Number]
             ))
        }
    }
    def provinceData(data: List[Hospital]): List[Map[String,String]] = {
        val provincelst = data map { x => Map("Province_Id" -> MD5.md5(x.getProvince_Name), "Province_Name" -> x.getProvince_Name)}
        provincelst.distinct
    }
    def cityData(data: List[Hospital]): List[Map[String, AnyRef]] = {
        val citylst = data map { x => Map("City_Id" -> MD5.md5(x.getCity_Name), "City_Name" -> x.getCity_Name, "City_Tier" -> (x.getCity_Tier match {
                    case "1" => pow(2,0).asInstanceOf[Number]
                    case "2" => pow(2,1).asInstanceOf[Number]
                    case "3" => pow(2,2).asInstanceOf[Number]
                    case "4" => pow(2,3).asInstanceOf[Number]
                    case "5" => pow(2,4).asInstanceOf[Number]
                })
        )}
        citylst.distinct
    }
    def specialtyData(data: List[Hospital]): List[Map[String,String]] = {
        val specialtylst =  data map { x => Map("Specialty_Id" -> MD5.md5(x.getSpecialty_Classification), "Specialty_Classification" -> x.getSpecialty_Classification)}
        specialtylst.distinct
    }
    def universityHospInfoData(data: List[Hospital]): List[Map[String, AnyRef]] = {
        data map { x =>
            Map("Hosp_Id" -> MD5.md5(x.getHosp_Name+x.getPha_Code),
                "doctorsnum" -> x.getDoctorsnum,
                "bedsnum" -> x.getBedsnum,
                "bedsnum_general_medicine" -> x.getBedsnum_general_medicine,
                "bedsnum_internal_medicine" -> x.getBedsnum_internal_medicine,
                "bedsnum_surgery_department" -> x.getBedsnum_surgery_department,
                "bedsnum_ophthalmology" -> x.getBedsnum_ophthalmology,
                "diagnosis_ppl_annual" -> x.getDiagnosis_ppl_annual,
                "diagnosis_amount_clinic" -> x.getDiagnosis_amount_clinic,
                "diagnosis_amount_internalmedicine" -> x.getDiagnosis_amount_internalmedicine,
                "diagnosis_amount_surgerydepartment" -> x.getDiagnosis_amount_surgerydepartment,
                "inpatient_ppl_annual" -> x.getInpatient_ppl_annual,
                "inpatient_operation_amount_annual" -> x.getInpatient_operation_amount_annual,
                "income_diagnosis_treatment" -> x.getIncome_diagnosis_treatment,
                "income_clinic" -> x.getIncome_clinic,
                "income_clinic_diagnosis_treatment" -> x.getIncome_clinic_diagnosis_treatment,
                "income_clinic_operation" -> x.getIncome_clinic_operation,
                "income_inpatient" -> x.getIncome_inpatient,
                "income_inpatient_beds" -> x.getIncome_inpatient_beds,
                "income_inpatient_diagnosis_treatment" -> x.getIncome_inpatient_diagnosis_treatment,
                "income_inpatient_operation" -> x.getIncome_inpatient_operation,
                "income_drugs" -> x.getIncome_drugs,
                "income_clinic_drugs" -> x.getIncome_clinic_drugs,
                "income_clinic_wst_drugs" -> x.getIncome_clinic_wst_drugs,
                "income_inpatient_drugs" -> x.getIncome_inpatient_drugs,
                "income_inpatient_wst_drugs" -> x.getIncome_inpatient_wst_drugs
           )
       }
    }
    
    def productsData(data: List[Products]):  Iterable[Map[String, AnyRef]] = {
        data.groupBy( x => x.getTrade_Name).map( y => Map("Prod_Id" -> MD5.md5(y._1), "Trade_Name" -> y._1, "Package_Quantity" -> y._2.map(z => z.getPackage_Quantity).distinct))
    }
    def dosageFormsData(data: List[Products]): List[Map[String, AnyRef]] = {
        val lst = data map { x=> Map("Dosageform_Id" -> MD5.md5(x.getDosageform), "Dosageform" -> Map("Ch" -> x.getDosageform, "En" -> ""))}
        lst.distinct
    }
    def drugspecificationData(data: List[Products]): List[Map[String, AnyRef]] = {
        val lst = data map { x => Map("Drugspecification_Id" -> MD5.md5(x.getDrugspecification), "Drugspecification" -> Map("Ch" -> "", "En" -> x.getDrugspecification))}
        lst.distinct
    }
    def manufacturerData(data: List[Products]): List[Map[String, AnyRef]] = {
        val lst = data map { x=> Map("Manufacturer_Id" -> MD5.md5(x.getManufacturer_Name),"Manufacturer_Name" -> Map("Ch" -> x.getManufacturer_Name, "En" -> ""))}
        lst.distinct
    }   
}

object AssemblyCoresData{
    def hospitalInfo(data: List[Hospital]): List[Map[String, AnyRef]] = {
        data map { x=> Map("HospInfo_Id" -> MD5.md5(x.getHosp_Name+x.getPha_Code),
            "Hospital" -> Map(
               "Hosp_Name" -> x.getHosp_Name, 
               "Pha_Code" -> x.getPha_Code, 
               "Tag" -> (
                    (x.getHosp_level match{
                        case "一级" => pow(2,2).asInstanceOf[Int]
                        case "二级" => pow(2,3).asInstanceOf[Int]
                        case "三级" => pow(2,4).asInstanceOf[Int]
                    })+
                    (x.getSpecialty match {
                        case "综合" => pow(2,5).asInstanceOf[Int]
                        case "专科" => pow(2,6).asInstanceOf[Int]
                    })+
                    (x.getIf_County match{
                        case "是" => pow(2,0).asInstanceOf[Int]
                        case "否" => pow(2,1).asInstanceOf[Int]
                    })).asInstanceOf[Number]),
            "Province" -> Map(
                    "Province_Name" -> x.getProvince_Name,
                    "City" -> Map(
                            "City_Name" -> x.getCity_Name,
                            "City_Tier" -> (x.getCity_Tier.toString() match {
                    case "1" => pow(2,0).asInstanceOf[Number]
                    case "2" => pow(2,1).asInstanceOf[Number]
                    case "3" => pow(2,2).asInstanceOf[Number]
                    case "4" => pow(2,3).asInstanceOf[Number]
                    case "5" => pow(2,4).asInstanceOf[Number]}))),
            "Specialty" -> x.getSpecialty_Classification)}
    }
    def regionData(data: List[Hospital]): Iterable[Map[String, AnyRef]] = {
        data.groupBy( x => (x.getRegion_Name)).map(a => Map(
            "Region_Id" -> MD5.md5(a._1),
            "Region_Name" -> a._1, 
            "Province_Name" -> a._2.groupBy( y => y.getProvince_Name ).map( b => b._1), 
            "City_lst" -> a._2.groupBy( z => (z.getCity_Name,z.getCity_Tier)).map( c => Map(
                    "City_Name" -> c._1._1,
                    "City_Tier" -> (c._1._2.toString() match {
                    case "1" => pow(2,0).asInstanceOf[Number]
                    case "2" => pow(2,1).asInstanceOf[Number]
                    case "3" => pow(2,2).asInstanceOf[Number]
                    case "4" => pow(2,3).asInstanceOf[Number]
                    case "5" => pow(2,4).asInstanceOf[Number]})
            ))))
    }
    def minimumProductInfo(data: List[Products]): List[Map[String, AnyRef]] = {
        data.distinct.map(a => Map(
            "MiniProdInfo_Id" -> MD5.md5(a.getTrade_Name+a.getDosageform+a.getDrugspecification+a.getPackage_Quantity+a.getManufacturer_Name), 
            "Products" -> Map(
                    "Trade_Name" -> Map("Ch" -> a.getTrade_Name,"En" -> ""),
                    "Package_Quantity" -> a.getPackage_Quantity), 
            "DosageForm" -> Map("Ch" -> a.getDosageform,"En" -> "") , 
            "DrugSpecification" -> Map("Ch" -> "","En" -> a.getDrugspecification), 
            "Manufacturer_Name" -> Map("Ch" -> a.getManufacturer_Name,"En" -> ""),
            "Genericname" -> Map("Ch" -> "","En" -> ""),
            "RouteOfMedication" -> Map("Ch" -> "En","" -> ""),
            "AtcCode" -> Map("Atc_Code" -> "","Atc_Code" -> "")
        ))
    }
}

