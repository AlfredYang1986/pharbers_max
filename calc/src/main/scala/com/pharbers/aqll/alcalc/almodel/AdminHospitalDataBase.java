package com.pharbers.aqll.alcalc.almodel;

/***
 * 医院数据库
 * @author Faiz
 *
 */
public class AdminHospitalDataBase {
	private String company;

    private Integer uploadYear = 0;

    private String market;

    private String segment;

    private String factor;

    private String ifPanelAll;

    private String ifPanelTouse;

    private Long hospId = 0L;

    private String hospName;

    private String phaid;

    private String ifCounty;

    private String hospLevel;

    private String region;

    private String province;

    private String prefecture;

    private String cityTier;

    private String specialty1;

    private String specialty2;

    private String reSpecialty;

    private String specialty3;

    private Double westMedicineIncome = 0.0;

    private Long doctorNum = 0L;

    private Long bedNum = 0L;

    private Long generalBedNum = 0L;

    private Long medicineBedNum = 0L;

    private Long surgeryBedNum = 0L;

    private Long ophthalmologyBedNum = 0L;

    private Long yearDiagnosisNum = 0L;

    private Long clinicNum = 0L;

    private Long medicineNum = 0L;

    private Long surgeryNum = 0L;

    private Long hospitalizedNum = 0L;

    private Long hospitalizedOpsNum = 0L;

    private Double income = 0.0;

    private Double clinicIncome = 0.0;

    private Double climicCureIncome = 0.0;
    
    private Double climicSurgicalIncome = 0.0;

    private Double hospitalizedIncome = 0.0;

    private Double hospitalizedBeiIncome = 0.0;

    private Double hospitalizedCireIncom = 0.0;

    private Double hospitalizedOpsIncome = 0.0;

    private Double drugIncome = 0.0;

    private Double climicDrugIncome = 0.0;

    private Double climicWestenIncome = 0.0;

    private Double hospitalizedDrugIncome = 0.0;

    private Double hospitalizedWestenIncome = 0.0;

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {if(!company.equals("#N/A")){this.company = company;} else {this.company = "";}}

	public Integer getUploadYear() {
		return uploadYear;
	}

	public void setUploadYear(Integer uploadYear) {
		this.uploadYear = uploadYear;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		if(!market.equals("#N/A")){this.market = market;} else {this.market = "";}

	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		if(!segment.equals("#N/A")){this.segment = segment;} else {this.segment = "";}

	}

	public String getFactor() {
		return factor;
	}

	public void setFactor(String factor) {if(!factor.equals("#N/A")){this.factor = factor;} else {this.factor = "";}}

	public String getIfPanelAll() {
		return ifPanelAll;
	}

	public void setIfPanelAll(String ifPanelAll) {if(!ifPanelAll.equals("#N/A")){this.ifPanelAll = ifPanelAll;} else {this.ifPanelAll = "";}}

	public String getIfPanelTouse() {
		return ifPanelTouse;
	}

	public void setIfPanelTouse(String ifPanelTouse) {if(!ifPanelTouse.equals("#N/A")){this.ifPanelTouse = ifPanelTouse;} else {this.ifPanelTouse = "";}}

	public Long getHospId() {
		return hospId;
	}

	public void setHospId(Long hospId) {
		this.hospId = hospId;
	}

	public String getHospName() {
		return hospName;
	}

	public void setHospName(String hospName) {if(!hospName.equals("#N/A")){this.hospName = hospName;} else {this.hospName = "";}}

	public String getPhaid() {
		return phaid;
	}

	public void setPhaid(String phaid) {if(!phaid.equals("#N/A")){this.phaid = phaid;} else {this.phaid = "";}}

	public String getIfCounty() {
		return ifCounty;
	}

	public void setIfCounty(String ifCounty) {if(!ifCounty.equals("#N/A")){this.ifCounty = ifCounty;} else {this.ifCounty = "";}}

	public String getHospLevel() {
		return hospLevel;
	}

	public void setHospLevel(String hospLevel) {if(!hospLevel.equals("#N/A")){this.hospLevel = hospLevel;} else {this.hospLevel = "";}}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {if(!region.equals("#N/A")){this.region = region;} else {this.region = "";}}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {if(!province.equals("#N/A")){this.province = province;} else {this.province = "";}}

	public String getPrefecture() {
		return prefecture;
	}

	public void setPrefecture(String prefecture) {if(!prefecture.equals("#N/A")){this.prefecture = prefecture;} else {this.prefecture = "";}}

	public String getCityTier() {
		return cityTier;
	}

	public void setCityTier(String cityTier) {if(!cityTier.equals("#N/A")){this.cityTier = cityTier;} else {this.cityTier = "";}}

	public String getSpecialty1() {
		return specialty1;
	}

	public void setSpecialty1(String specialty1) {if(!specialty1.equals("#N/A")){this.specialty1 = specialty1;} else {this.specialty1 = "";}}

	public String getSpecialty2() {
		return specialty2;
	}

	public void setSpecialty2(String specialty2) {if(!specialty2.equals("#N/A")){this.specialty2 = specialty2;} else {this.specialty2 = "";}}

	public String getReSpecialty() {
		return reSpecialty;
	}

	public void setReSpecialty(String reSpecialty) {if(!reSpecialty.equals("#N/A")){this.reSpecialty = reSpecialty;} else {this.reSpecialty = "";}}

	public String getSpecialty3() {
		return specialty3;
	}

	public void setSpecialty3(String specialty3) {if(!specialty3.equals("#N/A")){this.specialty3 = specialty3;} else {this.specialty3 = "";}}

	public Double getWestMedicineIncome() {
		return westMedicineIncome;
	}

	public void setWestMedicineIncome(Double westMedicineIncome) {this.westMedicineIncome = westMedicineIncome;}

	public Long getDoctorNum() {
		return doctorNum;
	}

	public void setDoctorNum(Long doctorNum) {
		this.doctorNum = doctorNum;
	}

	public Long getBedNum() {
		return bedNum;
	}

	public void setBedNum(Long bedNum) {
		this.bedNum = bedNum;
	}

	public Long getGeneralBedNum() {
		return generalBedNum;
	}

	public void setGeneralBedNum(Long generalBedNum) {
		this.generalBedNum = generalBedNum;
	}

	public Long getMedicineBedNum() {
		return medicineBedNum;
	}

	public void setMedicineBedNum(Long medicineBedNum) {
		this.medicineBedNum = medicineBedNum;
	}

	public Long getSurgeryBedNum() {
		return surgeryBedNum;
	}

	public void setSurgeryBedNum(Long surgeryBedNum) {
		this.surgeryBedNum = surgeryBedNum;
	}

	public Long getOphthalmologyBedNum() {
		return ophthalmologyBedNum;
	}

	public void setOphthalmologyBedNum(Long ophthalmologyBedNum) {
		this.ophthalmologyBedNum = ophthalmologyBedNum;
	}

	public Long getYearDiagnosisNum() {
		return yearDiagnosisNum;
	}

	public void setYearDiagnosisNum(Long yearDiagnosisNum) {
		this.yearDiagnosisNum = yearDiagnosisNum;
	}

	public Long getClinicNum() {
		return clinicNum;
	}

	public void setClinicNum(Long clinicNum) {
		this.clinicNum = clinicNum;
	}

	public Long getMedicineNum() {
		return medicineNum;
	}

	public void setMedicineNum(Long medicineNum) {
		this.medicineNum = medicineNum;
	}

	public Long getSurgeryNum() {
		return surgeryNum;
	}

	public void setSurgeryNum(Long surgeryNum) {
		this.surgeryNum = surgeryNum;
	}

	public Long getHospitalizedNum() {
		return hospitalizedNum;
	}

	public void setHospitalizedNum(Long hospitalizedNum) {
		this.hospitalizedNum = hospitalizedNum;
	}

	public Long getHospitalizedOpsNum() {
		return hospitalizedOpsNum;
	}

	public void setHospitalizedOpsNum(Long hospitalizedOpsNum) {
		this.hospitalizedOpsNum = hospitalizedOpsNum;
	}

	public Double getIncome() {
		return income;
	}

	public void setIncome(Double income) {
		this.income = income;
	}

	public Double getClinicIncome() {
		return clinicIncome;
	}

	public void setClinicIncome(Double clinicIncome) {
		this.clinicIncome = clinicIncome;
	}

	public Double getClimicCureIncome() {
		return climicCureIncome;
	}

	public void setClimicCureIncome(Double climicCureIncome) {
		this.climicCureIncome = climicCureIncome;
	}

	public Double getClimicSurgicalIncome() {
		return climicSurgicalIncome;
	}

	public void setClimicSurgicalIncome(Double climicSurgicalIncome) {
		this.climicSurgicalIncome = climicSurgicalIncome;
	}

	public Double getHospitalizedIncome() {
		return hospitalizedIncome;
	}

	public void setHospitalizedIncome(Double hospitalizedIncome) {
		this.hospitalizedIncome = hospitalizedIncome;
	}

	public Double getHospitalizedBeiIncome() {
		return hospitalizedBeiIncome;
	}

	public void setHospitalizedBeiIncome(Double hospitalizedBeiIncome) {
		this.hospitalizedBeiIncome = hospitalizedBeiIncome;
	}

	public Double getHospitalizedCireIncom() {
		return hospitalizedCireIncom;
	}

	public void setHospitalizedCireIncom(Double hospitalizedCireIncom) {
		this.hospitalizedCireIncom = hospitalizedCireIncom;
	}

	public Double getHospitalizedOpsIncome() {
		return hospitalizedOpsIncome;
	}

	public void setHospitalizedOpsIncome(Double hospitalizedOpsIncome) {
		this.hospitalizedOpsIncome = hospitalizedOpsIncome;
	}

	public Double getDrugIncome() {
		return drugIncome;
	}

	public void setDrugIncome(Double drugIncome) {
		this.drugIncome = drugIncome;
	}

	public Double getClimicDrugIncome() {
		return climicDrugIncome;
	}

	public void setClimicDrugIncome(Double climicDrugIncome) {
		this.climicDrugIncome = climicDrugIncome;
	}

	public Double getClimicWestenIncome() {
		return climicWestenIncome;
	}

	public void setClimicWestenIncome(Double climicWestenIncome) {
		this.climicWestenIncome = climicWestenIncome;
	}

	public Double getHospitalizedDrugIncome() {
		return hospitalizedDrugIncome;
	}

	public void setHospitalizedDrugIncome(Double hospitalizedDrugIncome) {
		this.hospitalizedDrugIncome = hospitalizedDrugIncome;
	}

	public Double getHospitalizedWestenIncome() {
		return hospitalizedWestenIncome;
	}

	public void setHospitalizedWestenIncome(Double hospitalizedWestenIncome) {
		this.hospitalizedWestenIncome = hospitalizedWestenIncome;
	}

}