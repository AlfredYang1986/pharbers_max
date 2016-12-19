package com.pharbers.aqll.excel.dispose

import com.pharbers.aqll.excel.model._
import scala.math._
import com.pharbers.aqll.util._

object AssemblyBasicData{
    def hospitalData(data: List[Hospital]): List[Map[String, AnyRef]] = {
        data.map( x => (
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
             )))
    }
    def provinceData(data: List[Hospital]): Iterable[Map[String,String]] = {
        data.groupBy(x => x.getProvince_Name).map(a => Map("Province_Id" -> MD5.md5(a._1), "Province_Name" -> a._1))
    }
    def cityData(data: List[Hospital]): List[Map[String, AnyRef]] = {
        data.map( x => Map("City_Id" -> MD5.md5(x.getCity_Name), "City_Name" -> x.getCity_Name, "City_Tier" -> (x.getCity_Tier match {
                    case "1" => pow(2,0).asInstanceOf[Number]
                    case "2" => pow(2,1).asInstanceOf[Number]
                    case "3" => pow(2,2).asInstanceOf[Number]
                    case "4" => pow(2,3).asInstanceOf[Number]
                    case "5" => pow(2,4).asInstanceOf[Number]
                })
        )).distinct
    }
    def specialtyData(data: List[Hospital]): Iterable[Map[String,String]] = {
        data.groupBy(x => x.getSpecialty_Classification).map(a => Map("Specialty_Id" -> MD5.md5(a._1), "Specialty_Classification" -> a._1))
    }
    def universityHospInfoData(data: List[Hospital]): List[Map[String, AnyRef]] = {
        data.map( x =>
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
           ))
    }
    
    def productsData(data: List[Products]):  Iterable[Map[String, AnyRef]] = {
        data.groupBy( x => x.getTrade_Name).map( a => Map("Prod_Id" -> MD5.md5(a._1), "Trade_Name" -> a._1, "Package_Quantity" -> a._2.groupBy(y => y.getPackage_Quantity).map(b => b._1)))
    }
    def dosageFormsData(data: List[Products]): Iterable[Map[String, AnyRef]] = {
        data.groupBy(x => x.getDosageform).map(a => Map("Dosageform_Id" -> MD5.md5(a._1), "Dosageform" -> Map("Ch" -> a._1, "En" -> "")))
    }
    def drugspecificationData(data: List[Products]): Iterable[Map[String, AnyRef]] = {
        data.groupBy(x => x.getDrugspecification).map(a => Map("Drugspecification_Id" -> MD5.md5(a._1), "Drugspecification" -> Map("Ch" -> "", "En" -> a._1)))
    }
    def manufacturerData(data: List[Products]): Iterable[Map[String, AnyRef]] = {
        data.groupBy(x => x.getManufacturer_Name).map(a => Map("Manufacturer_Id" -> MD5.md5(a._1),"Manufacturer_Name" -> Map("Ch" -> a._1, "En" -> "")))
    }   
}

object AssemblyCoresData{
    def hospitalInfo(data: List[Hospital]): List[Map[String, AnyRef]] = {
        data.map( x=> Map("HospInfo_Id" -> MD5.md5(x.getHosp_Name+x.getPha_Code),
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
            "Specialty" -> x.getSpecialty_Classification))
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
//        data.groupBy(x => x.getTrade_Name).map(a => Map(
//            "MiniProdInfo_Id" -> MD5.md5(a._1), 
//            "Products" -> Map("lst" -> (Map("Ch" -> a._1,"En" -> "") :: Nil),"Package_Quantity" -> (a._2.groupBy(y => y.getPackage_Quantity).map(b => b._1))), 
//            "Genericname" -> Map("lst" -> (Map("Ch" -> "", "En" -> "") :: Nil)), 
//            "DosageForm" -> Map("lst" -> a._2.groupBy(z => z.getDosageform).map(b => Map("Ch" -> b._1, "En" -> ""))), 
//            "DrugSpecification" -> Map("lst" -> a._2.groupBy(v => v.getDrugspecification).map(c => Map("Ch" -> "", "En" -> c._1))), 
//            "Manufacturer" -> Map("lst" -> a._2.groupBy(w => w.getManufacturer_Name).map(d => Map("Ch" -> d._1, "En" -> "")))
//        ))
        data.map( x=> Map(
                "MiniProdInfo_Id" -> MD5.md5(x.getTrade_Name+x.getDosageform+x.getDrugspecification+x.getPackage_Quantity+x.getManufacturer_Name),
                "Products" -> Map("Ch" -> x.getTrade_Name,"En" -> ""),
                "Drug" -> Map("Ch" -> "","En" -> ""),
                "DosageForm" -> Map("Ch" -> x.getDosageform,"En" -> ""),
                "DrugSpecification" -> Map("Ch" -> "","En" -> x.getDrugspecification),
                "RouteOfMedication" -> Map("Route" -> ""),
                "Manufacturer" -> Map("Ch" -> x.getManufacturer_Name,"En" -> ""),
                "AtcCode" -> Map("Atc_Code" -> "","Atc_Name" -> "")
        ))
    }
}

object CroesMatchData {
    def segmentMatch(data: List[SegmentInfo]): Iterable[Map[String,AnyRef]] = {
        data.groupBy(x=> x.getSegment).map(a => Map(
            "SegmentDefinition_Id" -> MD5.md5(a._1.toString()),
            "Segment_lst" -> (MD5.md5(a._1.toString()) :: Nil), 
            "HospitalInfo_lst" -> a._2.map(b => MD5.md5(b.getHosp_Name+b.getPha_Code))
        ))
    }
}

object BasicMatchData {
    
    def atccodeMatch(data: List[AtcCode]):List[Map[String,AnyRef]] = {
        data.map(x => Map("Atccode_Id" -> MD5.md5(x.getAtcCode), "Atc_Code" -> x.getAtcCode, "Atc_Name" -> x.getAtcCodeName_En))
    }
    
    def durgMatch(data: List[Durg]):List[Map[String,AnyRef]] = {
        data.map(x => Map("Dosageform_Id" -> MD5.md5(x.getGenericName), "Genericname" -> Map("Ch" -> x.getGenericName,"En" -> "")))
    }
    
    def routeofmedicationMatch(data: List[Routeofmedication]):Iterable[Map[String,AnyRef]] = {
        data map {x => Map(
               "Route_Id" -> MD5.md5(x.getRouteofmedication), "Route" -> x.getRouteofmedication
        )}
    }
    
    def segmentbasicMatch(data: List[SegmentBasic]):List[Map[String,AnyRef]] = {
        data map { x => Map(
            "Segment_Id" -> MD5.md5(x.getSegment.toString()), "Segment_Code" -> x.getSegment   
        )}
    }
    
    def datasourceMatch():List[Map[String,AnyRef]] = {
        (Map("Datasource_Id" -> MD5.md5("CPA"), "Datasource_Type" -> "CPA", "File_Path" -> "", "Creation_Date" -> "") :: 
         Map("Datasource_Id" -> MD5.md5("PharmaTrust"), "Datasource_Type" -> "PharmaTrust", "File_Path" -> "", "Creation_Date" -> "") :: Nil)
    }
    
    def companyMatch():List[Map[String,AnyRef]] = {
        (Map("Company_Id" -> MD5.md5("Astellas"),"Company_Name" -> "Astellas","Usr_lst" -> (Map("User_Id" -> MD5.md5("Guest"),"User_Account" -> "Guest","User_Name" -> "Guest") :: Nil)) ::
         Map("Company_Id" -> MD5.md5("Pfizer"),"Company_Name" -> "Astellas","Usr_lst" -> (Map("User_Id" -> MD5.md5("Guest"),"User_Account" -> "Guest","User_Name" -> "Guest") :: Nil)) ::
         Map("Company_Id" -> MD5.md5("Bayer"),"Company_Name" -> "Astellas","Usr_lst" -> (Map("User_Id" -> MD5.md5("Guest"),"User_Account" -> "Guest","User_Name" -> "Guest") :: Nil)) ::
         Map("Company_Id" -> MD5.md5("BMS"),"Company_Name" -> "Astellas","Usr_lst" -> (Map("User_Id" -> MD5.md5("Guest"),"User_Account" -> "Guest","User_Name" -> "Guest") :: Nil)) :: Nil)
    }
    
    def marketMatch():List[Map[String,AnyRef]] = {
        (Map("Market_Id" -> MD5.md5("降压药市场"), "Market_Code" -> Map("Ch" -> "降压药市场", "En" -> "")) ::
         Map("Market_Id" -> MD5.md5("ACEI"), "Market_Code" -> Map("Ch" -> "ACEI", "En" -> "")) ::
         Map("Market_Id" -> MD5.md5("CCB"), "Market_Code" -> Map("Ch" -> "CCB", "En" -> "")) ::
         Map("Market_Id" -> MD5.md5("高血压市场"), "Market_Code" -> Map("Ch" -> "高血压市场", "En" -> "")) :: Nil
        )
    }
}

