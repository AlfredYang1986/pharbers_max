package com.pharbers.aqll.calc.excel.CPA;

/***
 * CPA产品
 * @author Faiz
 *
 */
import com.pharbers.aqll.calc.excel.common.commonProductObjectTrait;
import com.pharbers.aqll.calc.util.StringOption;

public class CpaProduct extends commonProductObjectTrait {

	private String province;

	private String city;

	private String area;

	private Integer uploadYear;

	private Integer uploadMonth;

	private Long hospNum;

	private String atcNum;

	private String drugsname;

	private String tradename;

	private String packingunits;

	private String drugspecifications;

	private String numberPackaging;

	private Double sumValue;

	private Double volumeUnit;

	private String dosageforms;

	private String routeAdministration;

	private String manufacturer;

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

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
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

	public String getAtcNum() {
		return atcNum;
	}

	public void setAtcNum(String atcNum) {
		this.atcNum = atcNum;
	}

	public String getDrugsname() {
		return drugsname;
	}

	public void setDrugsname(String drugsname) {
		this.drugsname = drugsname;
	}

	public String getTradename() {
		return tradename;
	}

	public void setTradename(String tradename) {
		this.tradename = tradename;
	}

	public String getPackingunits() {
		return packingunits;
	}

	public void setPackingunits(String packingunits) {
		this.packingunits = packingunits;
	}

	public String getDrugspecifications() {
		return drugspecifications;
	}

	public void setDrugspecifications(String drugspecifications) {
		this.drugspecifications = drugspecifications;
	}

	public String getNumberPackaging() {
		return numberPackaging;
	}

	public void setNumberPackaging(String numberPackaging) {
		this.numberPackaging = numberPackaging;
	}

	public Long getHospNum() {
		return hospNum;
	}

	public void setHospNum(Long hospNum) {
		this.hospNum = hospNum;
	}

	public Double getSumValue() {
		return sumValue;
	}

	public void setSumValue(Double sumValue) {
		this.sumValue = sumValue;
	}

	public Double getVolumeUnit() {
		return volumeUnit;
	}

	public void setVolumeUnit(Double volumeUnit) {
		this.volumeUnit = volumeUnit;
	}

	public String getDosageforms() {
		return dosageforms;
	}

	public void setDosageforms(String dosageforms) {
		this.dosageforms = dosageforms;
	}

	public String getRouteAdministration() {
		return routeAdministration;
	}

	public void setRouteAdministration(String routeAdministration) {
		this.routeAdministration = routeAdministration;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

    public String commonObjectCondition() {
    	return StringOption.takeStringSpace(
    			this.getTradename() +
    	        this.getManufacturer() +
    	        this.getDosageforms() +
    	        this.getDrugspecifications() +
    	        this.getNumberPackaging());
    }

	/*@Override
	public String toString() {
		return "CpaProduct [province=" + province + ", city=" + city
				+ ", area=" + area + ", uploadYear=" + uploadYear
				+ ", uploadQuarter=" + uploadQuarter + ", uploadMonth="
				+ uploadMonth + ", hospNum=" + hospNum + ", " + ", atcNum="
				+ atcNum + ", drugsname=" + drugsname + ", tradename="
				+ tradename + ", " + ", packingunits=" + packingunits
				+ ", drugspecifications=" + drugspecifications
				+ ", numberPackaging=" + numberPackaging + ", " + ", sumValue="
				+ sumValue + ", volumeUnit=" + volumeUnit + ", dosageforms="
				+ dosageforms + ", " + ", routeAdministration="
				+ routeAdministration + ", manufacturer=" + manufacturer
				+ "]";
	}*/
}