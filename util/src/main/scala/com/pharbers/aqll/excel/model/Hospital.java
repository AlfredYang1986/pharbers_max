package com.pharbers.aqll.excel.model;

public class Hospital {
	private String Hosp_Name;
	private String Pha_Code;
	private String If_County;
	private String Hosp_level;
	private String Region_Name;
	private String Province_Name;
	private String City_Name;
	private String City_Tier;
	private String Specialty;
	private String Specialty_Classification;
	private Integer doctorsnum;
	private Integer bedsnum;
	private Integer bedsnum_general_medicine;
	private Integer bedsnum_internal_medicine;
	private Integer bedsnum_surgery_department;
	private Integer bedsnum_ophthalmology;
	private Integer diagnosis_ppl_annual;
	private Integer diagnosis_amount_clinic;
	private Integer diagnosis_amount_internalmedicine;
	private Integer diagnosis_amount_surgerydepartment;
	private Integer inpatient_ppl_annual;
	private Integer inpatient_operation_amount_annual;
	private Double income_diagnosis_treatment;
	private Double income_clinic;
	private Double income_clinic_diagnosis_treatment;
	private Double income_clinic_operation;
	private Double income_inpatient;
	private Double income_inpatient_beds;
	private Double income_inpatient_diagnosis_treatment;
	private Double income_inpatient_operation;
	private Double income_drugs;
	private Double income_clinic_drugs;
	private Double income_clinic_wst_drugs;
	private Double income_inpatient_drugs;
	private Double income_inpatient_wst_drugs;
	
	public String getHosp_Name() {
		return Hosp_Name;
	}
	public void setHosp_Name(String hosp_Name) {
		Hosp_Name = hosp_Name;
	}
	public String getPha_Code() {
		return Pha_Code;
	}
	public void setPha_Code(String pha_Code) {
		Pha_Code = pha_Code;
	}
	public String getIf_County() {
		return If_County;
	}
	public void setIf_County(String if_County) {
		If_County = if_County;
	}
	public String getHosp_level() {
		return Hosp_level;
	}
	public void setHosp_level(String hosp_level) {
		Hosp_level = hosp_level;
	}
	public String getRegion_Name() {
		return Region_Name;
	}
	public void setRegion_Name(String region_Name) {
		Region_Name = region_Name;
	}
	public String getProvince_Name() {
		return Province_Name;
	}
	public void setProvince_Name(String province_Name) {
		Province_Name = province_Name;
	}
	public String getCity_Name() {
		return City_Name;
	}
	public void setCity_Name(String city_Name) {
		City_Name = city_Name;
	}
	public String getCity_Tier() {
		return City_Tier;
	}
	public void setCity_Tier(String city_Tier) {
		City_Tier = city_Tier;
	}
	public String getSpecialty() {
		return Specialty;
	}
	public void setSpecialty(String specialty) {
		Specialty = specialty;
	}
	public String getSpecialty_Classification() {
		return Specialty_Classification;
	}
	public void setSpecialty_Classification(String specialty_Classification) {
		Specialty_Classification = specialty_Classification;
	}
	public Integer getDoctorsnum() {
		return doctorsnum;
	}
	public void setDoctorsnum(Integer doctorsnum) {
		this.doctorsnum = doctorsnum;
	}
	public Integer getBedsnum() {
		return bedsnum;
	}
	public void setBedsnum(Integer bedsnum) {
		this.bedsnum = bedsnum;
	}
	public Integer getBedsnum_general_medicine() {
		return bedsnum_general_medicine;
	}
	public void setBedsnum_general_medicine(Integer bedsnum_general_medicine) {
		this.bedsnum_general_medicine = bedsnum_general_medicine;
	}
	public Integer getBedsnum_internal_medicine() {
		return bedsnum_internal_medicine;
	}
	public void setBedsnum_internal_medicine(Integer bedsnum_internal_medicine) {
		this.bedsnum_internal_medicine = bedsnum_internal_medicine;
	}
	public Integer getBedsnum_surgery_department() {
		return bedsnum_surgery_department;
	}
	public void setBedsnum_surgery_department(Integer bedsnum_surgery_department) {
		this.bedsnum_surgery_department = bedsnum_surgery_department;
	}
	public Integer getBedsnum_ophthalmology() {
		return bedsnum_ophthalmology;
	}
	public void setBedsnum_ophthalmology(Integer bedsnum_ophthalmology) {
		this.bedsnum_ophthalmology = bedsnum_ophthalmology;
	}
	public Integer getDiagnosis_ppl_annual() {
		return diagnosis_ppl_annual;
	}
	public void setDiagnosis_ppl_annual(Integer diagnosis_ppl_annual) {
		this.diagnosis_ppl_annual = diagnosis_ppl_annual;
	}
	public Integer getDiagnosis_amount_clinic() {
		return diagnosis_amount_clinic;
	}
	public void setDiagnosis_amount_clinic(Integer diagnosis_amount_clinic) {
		this.diagnosis_amount_clinic = diagnosis_amount_clinic;
	}
	public Integer getDiagnosis_amount_internalmedicine() {
		return diagnosis_amount_internalmedicine;
	}
	public void setDiagnosis_amount_internalmedicine(
			Integer diagnosis_amount_internalmedicine) {
		this.diagnosis_amount_internalmedicine = diagnosis_amount_internalmedicine;
	}
	public Integer getDiagnosis_amount_surgerydepartment() {
		return diagnosis_amount_surgerydepartment;
	}
	public void setDiagnosis_amount_surgerydepartment(
			Integer diagnosis_amount_surgerydepartment) {
		this.diagnosis_amount_surgerydepartment = diagnosis_amount_surgerydepartment;
	}
	public Integer getInpatient_ppl_annual() {
		return inpatient_ppl_annual;
	}
	public void setInpatient_ppl_annual(Integer inpatient_ppl_annual) {
		this.inpatient_ppl_annual = inpatient_ppl_annual;
	}
	public Integer getInpatient_operation_amount_annual() {
		return inpatient_operation_amount_annual;
	}
	public void setInpatient_operation_amount_annual(
			Integer inpatient_operation_amount_annual) {
		this.inpatient_operation_amount_annual = inpatient_operation_amount_annual;
	}
	public Double getIncome_diagnosis_treatment() {
		return income_diagnosis_treatment;
	}
	public void setIncome_diagnosis_treatment(Double income_diagnosis_treatment) {
		this.income_diagnosis_treatment = income_diagnosis_treatment;
	}
	public Double getIncome_clinic() {
		return income_clinic;
	}
	public void setIncome_clinic(Double income_clinic) {
		this.income_clinic = income_clinic;
	}
	public Double getIncome_clinic_diagnosis_treatment() {
		return income_clinic_diagnosis_treatment;
	}
	public void setIncome_clinic_diagnosis_treatment(
			Double income_clinic_diagnosis_treatment) {
		this.income_clinic_diagnosis_treatment = income_clinic_diagnosis_treatment;
	}
	public Double getIncome_clinic_operation() {
		return income_clinic_operation;
	}
	public void setIncome_clinic_operation(Double income_clinic_operation) {
		this.income_clinic_operation = income_clinic_operation;
	}
	public Double getIncome_inpatient() {
		return income_inpatient;
	}
	public void setIncome_inpatient(Double income_inpatient) {
		this.income_inpatient = income_inpatient;
	}
	public Double getIncome_inpatient_beds() {
		return income_inpatient_beds;
	}
	public void setIncome_inpatient_beds(Double income_inpatient_beds) {
		this.income_inpatient_beds = income_inpatient_beds;
	}
	public Double getIncome_inpatient_diagnosis_treatment() {
		return income_inpatient_diagnosis_treatment;
	}
	public void setIncome_inpatient_diagnosis_treatment(
			Double income_inpatient_diagnosis_treatment) {
		this.income_inpatient_diagnosis_treatment = income_inpatient_diagnosis_treatment;
	}
	public Double getIncome_inpatient_operation() {
		return income_inpatient_operation;
	}
	public void setIncome_inpatient_operation(Double income_inpatient_operation) {
		this.income_inpatient_operation = income_inpatient_operation;
	}
	public Double getIncome_drugs() {
		return income_drugs;
	}
	public void setIncome_drugs(Double income_drugs) {
		this.income_drugs = income_drugs;
	}
	public Double getIncome_clinic_drugs() {
		return income_clinic_drugs;
	}
	public void setIncome_clinic_drugs(Double income_clinic_drugs) {
		this.income_clinic_drugs = income_clinic_drugs;
	}
	public Double getIncome_clinic_wst_drugs() {
		return income_clinic_wst_drugs;
	}
	public void setIncome_clinic_wst_drugs(Double income_clinic_wst_drugs) {
		this.income_clinic_wst_drugs = income_clinic_wst_drugs;
	}
	public Double getIncome_inpatient_drugs() {
		return income_inpatient_drugs;
	}
	public void setIncome_inpatient_drugs(Double income_inpatient_drugs) {
		this.income_inpatient_drugs = income_inpatient_drugs;
	}
	public Double getIncome_inpatient_wst_drugs() {
		return income_inpatient_wst_drugs;
	}
	public void setIncome_inpatient_wst_drugs(Double income_inpatient_wst_drugs) {
		this.income_inpatient_wst_drugs = income_inpatient_wst_drugs;
	}
}
