package com.pharbers.aqll.calc.excel.Manage;

/***
 * 样本医院匹配
 * @author Faiz
 *
 */
public class AdminHospitalMatchingData {
	private String datasource;

	private Long hospNum = 0L;

	private String hospNameCh;

	private String hospNameEn;

	private String hospLevelCh;

	private String hospLevelEn;

	private String areaCh;

	private String areaEn;

	private String provinceCh;

	private String provinceEn;

	private String cityCh;

	private String cityEn;

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		if (!datasource.equals("#N/A")) {
			this.datasource = datasource;
		} else {
			this.datasource = "";
		}
	}

	public Long getHospNum() {
		return hospNum;
	}

	public void setHospNum(Long hospNum) {
		this.hospNum = hospNum;
	}

	public String getHospNameCh() {
		return hospNameCh;
	}

	public void setHospNameCh(String hospNameCh) {
		if (!hospNameCh.equals("#N/A")) {
			this.hospNameCh = hospNameCh;
		} else {
			this.hospNameCh = "";
		}
	}

	public String getHospNameEn() {
		return hospNameEn;
	}

	public void setHospNameEn(String hospNameEn) {if(!hospNameEn.equals("#N/A")){this.hospNameEn = hospNameEn;} else {this.hospNameEn = "";}}

	public String getHospLevelCh() {
		return hospLevelCh;
	}

	public void setHospLevelCh(String hospLevelCh) {if(!hospLevelCh.equals("#N/A")){this.hospLevelCh = hospLevelCh;} else {this.hospLevelCh = "";}}

	public String getHospLevelEn() {
		return hospLevelEn;
	}

	public void setHospLevelEn(String hospLevelEn) {if(!hospLevelEn.equals("#N/A")){this.hospLevelEn = hospLevelEn;} else {this.hospLevelEn = "";}}

	public String getAreaCh() {
		return areaCh;
	}

	public void setAreaCh(String areaCh) {if(!areaCh.equals("#N/A")){this.areaCh = areaCh;} else {this.areaCh = "";}}

	public String getAreaEn() {
		return areaEn;
	}

	public void setAreaEn(String areaEn) {if(!areaEn.equals("#N/A")){this.areaEn = areaEn;} else {this.areaEn = "";}}

	public String getProvinceCh() {
		return provinceCh;
	}

	public void setProvinceCh(String provinceCh) {if(!provinceCh.equals("#N/A")){this.provinceCh = provinceCh;} else {this.provinceCh = "";}}

	public String getProvinceEn() {
		return provinceEn;
	}

	public void setProvinceEn(String provinceEn) {if(!provinceEn.equals("#N/A")){this.provinceEn = provinceEn;} else {this.provinceEn = "";}}

	public String getCityCh() {
		return cityCh;
	}

	public void setCityCh(String cityCh) {if(!cityCh.equals("#N/A")){this.cityCh = cityCh;} else {this.cityCh = "";}}

	public String getCityEn() {
		return cityEn;
	}

	public void setCityEn(String cityEn) {if(!cityEn.equals("#N/A")){this.cityEn = cityEn;} else {this.cityEn = "";}}
}