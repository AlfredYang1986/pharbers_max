package com.pharbers.aqll.excel.model;

public class SegmentInfo {
	private Integer segment;
    private String Hosp_Name;
    private String Pha_Code;
    
	public Integer getSegment() {
		return segment;
	}
	public void setSegment(Integer segment) {
		this.segment = segment;
	}
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
}
