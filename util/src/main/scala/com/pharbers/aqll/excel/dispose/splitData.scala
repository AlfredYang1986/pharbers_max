package com.pharbers.aqll.excel.dispose

import com.pharbers.aqll.excel.model._

object splitData{

    //医院Collections
    def hospitalData(data: List[Hospital]): List[List[String]] = {
        data map { x => (x.getHosp_Name :: x.getPha_Code :: x.getHosp_level :: x.getSpecialty :: x.getIf_County :: Nil)}
    }
    //省份Collections
    def provinceData(data: List[Hospital]): List[List[String]] = {
        data map { x => (x.getProvince_Name :: Nil)}
    }
    //城市Collections
    def cityData(data: List[Hospital]): List[List[Any]] = {
        data map { x => (x.getCity_Name :: x.getCity_Tier :: Nil)}
    }
    //特殊专科Collections
    def specialtyData(data: List[Hospital]): List[List[String]] = {
        data map { x => (x.getSpecialty_Classification :: Nil)}
    }
    //计算变量Collections
    def universityHospInfoData(data: List[Hospital]): List[List[Any]] = {
        data map { x =>
            (x.getHosp_Name ::
            x.getPha_Code ::
            x.getDoctorsnum ::
          	x.getBedsnum ::
          	x.getBedsnum_general_medicine ::
          	x.getBedsnum_internal_medicine ::
          	x.getBedsnum_surgery_department ::
          	x.getBedsnum_ophthalmology ::
          	x.getDiagnosis_ppl_annual ::
          	x.getDiagnosis_amount_clinic ::
          	x.getDiagnosis_amount_internalmedicine ::
          	x.getDiagnosis_amount_surgerydepartment ::
          	x.getInpatient_ppl_annual ::
          	x.getInpatient_operation_amount_annual ::
          	x.getIncome_diagnosis_treatment ::
          	x.getIncome_clinic ::
          	x.getIncome_clinic_diagnosis_treatment ::
          	x.getIncome_clinic_operation ::
          	x.getIncome_inpatient ::
          	x.getIncome_inpatient_beds ::
          	x.getIncome_inpatient_diagnosis_treatment ::
          	x.getIncome_inpatient_operation ::
          	x.getIncome_drugs ::
          	x.getIncome_clinic_drugs ::
          	x.getIncome_clinic_wst_drugs ::
          	x.getIncome_inpatient_drugs::
          	x.getIncome_inpatient_wst_drugs :: Nil)
        }
    }
    
    //产品Collections
    def productsData(data: List[Products]): List[List[Any]] = {
        data map { x => (x.getTrade_Name :: x.getPackage_Quantity :: Nil)}
    }
    //剂型Collections
    def dosageFormsData(data: List[Products]): List[List[String]] = {
        data map { x => (x.getDosageform :: Nil)}
    }
    //药品规格Collections
    def drugspecificationData(data: List[Products]): List[List[String]] = {
        data map { x => (x.getDrugspecification :: Nil)}
    }
    //生成企业Collections
    def manufacturerData(data: List[Products]): List[List[String]] = {
        data map { x => (x.getManufacturer_Name :: Nil)}
    }   
}

