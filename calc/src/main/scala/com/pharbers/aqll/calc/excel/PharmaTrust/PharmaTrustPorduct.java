package com.pharbers.aqll.calc.excel.PharmaTrust;

//import com.pharbers.max.util.StringOption;
import com.pharbers.aqll.calc.excel.common.commonProductObjectTrait;

public class PharmaTrustPorduct extends commonProductObjectTrait {

    private String province;

    private String city;

    private Integer uploadYear;
    
    private Integer uploadMonth;

    private Long hospNum;

    private String hospLevel;

    private String generalname;

    private String tradename;

    private String drugspecifications;

    private String dosageforms;

    private String numberPackaging;

    private String routeAdministration;

    private Double volumeUnit;

    private Double sumValue;

    private String manufacturer;

    private String minimumUnit;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getUploadYear() {
		return uploadYear;
	}

	public void setUploadYear(Integer uploadYear) {
		this.uploadYear = uploadYear;
	}

	public Integer getUploadMonth() {
		return uploadMonth;
	}

	public void setUploadMonth(Integer uploadMonth) {
		this.uploadMonth = uploadMonth;
	}

	public Long getHospNum() {
		return hospNum;
	}

	public void setHospNum(Long hospNum) {
		this.hospNum = hospNum;
	}

	public String getHospLevel() {
		return hospLevel;
	}

	public void setHospLevel(String hospLevel) {
		this.hospLevel = hospLevel;
	}

	public String getGeneralname() {
		return generalname;
	}

	public void setGeneralname(String generalname) {
		this.generalname = generalname;
	}

	public String getTradename() {
		return tradename;
	}

	public void setTradename(String tradename) {
		this.tradename = tradename;
	}

	public String getDrugspecifications() {
		return drugspecifications;
	}

	public void setDrugspecifications(String drugspecifications) {
		this.drugspecifications = drugspecifications;
	}

	public String getDosageforms() {
		return dosageforms;
	}

	public void setDosageforms(String dosageforms) {
		this.dosageforms = dosageforms;
	}

	public String getNumberPackaging() {
		return numberPackaging;
	}

	public void setNumberPackaging(String numberPackaging) {
		this.numberPackaging = numberPackaging;
	}

	public String getRouteAdministration() {
		return routeAdministration;
	}

	public void setRouteAdministration(String routeAdministration) {
		this.routeAdministration = routeAdministration;
	}

	public Double getVolumeUnit() {
		return volumeUnit;
	}

	public void setVolumeUnit(Double volumeUnit) {
		this.volumeUnit = volumeUnit;
	}

	public Double getSumValue() {
		return sumValue;
	}

	public void setSumValue(Double sumValue) {
		this.sumValue = sumValue;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getMinimumUnit() {
		return minimumUnit;
	}

	public void setMinimumUnit(String minimumUnit) {
		this.minimumUnit = minimumUnit;
	}
   
    public String commonObjectCondition() {
//    	return StringOption.takeStringSpace(
    	return (
            this.getTradename() + this.getManufacturer() + this.getDosageforms() + this.getDrugspecifications() + this.getNumberPackaging());
    }
}