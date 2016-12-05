package com.pharbers.aqll.calc.excel.CPA;

import java.io.Serializable;

import com.pharbers.aqll.calc.excel.common.commonMarketObjectTrait;


public class CpaMarket extends commonMarketObjectTrait implements Serializable {
	private static final long serialVersionUID = 999L;

	private String province;

    private String city;

    private Integer uploadYear;

    private Integer uploadMonth;

    private Long hospNum;

    private String marketname;

    private Double volumeUnit;

    private Double sumValue;

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

	public String getMarketname() {
		return marketname;
	}

	public void setMarketname(String marketname) {
		this.marketname = marketname;
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
}