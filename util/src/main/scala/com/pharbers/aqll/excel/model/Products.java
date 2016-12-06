package com.pharbers.aqll.excel.model;

public class Products {
	private String Trade_Name;
	private String Dosageform;
	private String Drugspecification;
	private Integer Package_Quantity;
	private String Manufacturer_Name;
	
	public String getTrade_Name() {
		return Trade_Name;
	}
	public void setTrade_Name(String trade_Name) {
		Trade_Name = trade_Name;
	}
	public String getDosageform() {
		return Dosageform;
	}
	public void setDosageform(String dosageform) {
		Dosageform = dosageform;
	}
	public String getDrugspecification() {
		return Drugspecification;
	}
	public void setDrugspecification(String drugspecification) {
		Drugspecification = drugspecification;
	}
	public Integer getPackage_Quantity() {
		return Package_Quantity;
	}
	public void setPackage_Quantity(Integer package_Quantity) {
		Package_Quantity = package_Quantity;
	}
	public String getManufacturer_Name() {
		return Manufacturer_Name;
	}
	public void setManufacturer_Name(String manufacturer_Name) {
		Manufacturer_Name = manufacturer_Name;
	}
}
