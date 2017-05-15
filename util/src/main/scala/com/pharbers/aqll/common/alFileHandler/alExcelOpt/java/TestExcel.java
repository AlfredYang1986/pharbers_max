package com.pharbers.aqll.common.alFileHandler.alExcelOpt.java;

/**
 * Created by qianpeng on 2017/5/15.
 */
public class TestExcel {

	public TestExcel(String name, Integer age, String address) {
		this.name = name;
		this.age = age;
		this.address = address;
	}

	@Excel(name = "姓名")
	private String name;

	@Excel(name = "年龄")
	private Integer age;

	@Excel(name = "地址")
	private String address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer agr) {
		this.age = agr;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
