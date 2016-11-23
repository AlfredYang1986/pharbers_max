package com.pharbers.aqll.calc.excel.helpFunc;


public class RunDate {
	public static double startDate(){
		double startDate = System.currentTimeMillis();
		return startDate;
	}
	
	public static double endDate(String content,double startDate){
		double endDate = System.currentTimeMillis();
		System.out.println(content+"耗时："+((endDate-startDate)/1000)+"秒\n");
		return endDate;
	}
}
