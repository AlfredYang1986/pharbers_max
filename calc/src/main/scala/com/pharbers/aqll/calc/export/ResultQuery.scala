package com.pharbers.aqll.calc.export

import java.text.SimpleDateFormat
import com.mongodb.casbah.Imports._
import scala.collection.immutable.List
import com.pharbers.aqll.calc.export.DataWriteIn._

/**
  * Created by Wli on 2017/3/8 0008.
  */
object ResultQuery {
//	var map = Map("datatype" -> "省份数据","company" -> "098f6bcd4621d373cade4e832627b4f6","market" -> "AI_R#","staend" -> "01/2015#12/2016#")
//	finalresult_func(map)
	/*Stitching query conditions*/
	def finalresult_func(data : Map[String,Any]) : List[Map[String,Any]] = {
		try{
			var markets = data.get("market").get.asInstanceOf[String].split("#").filter(!_.equals(""))
			val datetiem = data.get("staend").get.asInstanceOf[String].split("#")
			val fm = new SimpleDateFormat("MM/yyyy")
			var conditions = List("Date" $gte fm.parse(datetiem.head).getTime $lte fm.parse(datetiem.tail.head).getTime)
			if(markets.size!=0){
				conditions = List("Date" $gte fm.parse(datetiem.head).getTime $lte fm.parse(datetiem.tail.head).getTime,"Market" $in markets)
			}
			println(s"conditions=${conditions}")
			val fileName = WriteDataToCSV(data,conditions)
			println(s"fileName=$fileName")
		}catch {
			case ex : Exception => println("Written to the file anomalies.")
		}
		List(Map("result" -> "Success"))
	}
}