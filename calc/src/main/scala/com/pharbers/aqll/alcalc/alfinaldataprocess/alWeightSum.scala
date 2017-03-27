package com.pharbers.aqll.alcalc.alfinaldataprocess

import java.util.UUID

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.util.dao.{_data_connection_cores, from}
import com.pharbers.aqll.util.MD5
/**
  * Created by LIWEI on 17-3-17.
  */
object alWeightSum extends App{
	def apply(company : String): alWeightSum = new alWeightSum(company)
}

class alWeightSum(company : String){
	val lst = (from db() in company+"_temp").selectOneByOne("hosp_Index")(x => x)(_data_connection_cores)
	var b : Option[DBObject] = None
	var f_units_sum,f_sales_sum = 0.0
	var f_units_sum2,f_sales_sum2 = 0.0
	var i = 0
	val total = lst.size
	while(lst.hasNext) {
		val c : DBObject = lst.next()
		i = i+1
		b match {
			case None => {
				b = Some(c)
				f_units_sum = c.get("f_units").asInstanceOf[Double]
				f_sales_sum = c.get("f_sales").asInstanceOf[Double]
				// TODO : 过滤f_units|f_sales和为0.0的数据
				if(total==i && f_units_sum!=0.0 && f_sales_sum!=0.0){
					if(_data_connection_cores.getCollection(company).count()==1){
						_data_connection_cores.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))
					}
					// TODO : 还原省份|城市权和索引，其他字段保持不变
					_data_connection_cores.getCollection(company).insert(Map("ID" -> MD5.md5(UUID.randomUUID().toString) ,"Provice" -> c.get("Provice"),"City" -> c.get("City"),"Panel_ID" -> c.get("Panel_ID"),"Market" -> c.get("Market"),"Product" -> c.get("Product"),"f_units" -> f_units_sum,"f_sales" -> f_sales_sum,"Date" -> c.get("Date"),"hosp_Index" -> c.get("hosp_Index"),
								"prov_Index" -> MD5.md5(c.get("Provice")+c.get("Market").toString+c.get("Product")+c.get("Date")),
								"city_Index" -> MD5.md5(c.get("Provice")+c.get("City").toString+c.get("Market")+c.get("Product")+c.get("Date"))
								))
				}
			}
			case Some(x) => {
				var flag = x.get("hosp_Index").equals(c.get("hosp_Index"))
				flag match {
					case true => {
						f_units_sum = f_units_sum + c.get("f_units").asInstanceOf[Double]
						f_sales_sum = f_sales_sum + c.get("f_sales").asInstanceOf[Double]
						// TODO : 过滤f_units|f_sales和为0.0的数据
						if(total==i && f_units_sum!=0.0 && f_sales_sum!=0.0){
							if(_data_connection_cores.getCollection(company).count()==1){
								_data_connection_cores.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))
							}
							// TODO : 还原省份|城市权和索引，其他字段保持不变
							_data_connection_cores.getCollection(company).insert(Map("ID" -> MD5.md5(UUID.randomUUID().toString) ,"Provice" -> x.get("Provice"),"City" -> x.get("City"),"Panel_ID" -> x.get("Panel_ID"),"Market" -> x.get("Market"),"Product" -> x.get("Product"),"f_units" -> f_units_sum,"f_sales" -> f_sales_sum,"Date" -> x.get("Date"),"hosp_Index" -> x.get("hosp_Index"),
								"prov_Index" -> MD5.md5(x.get("Provice")+x.get("Market").toString+x.get("Product")+x.get("Date")),
								"city_Index" -> MD5.md5(x.get("Provice")+x.get("City").toString+x.get("Market")+x.get("Product")+x.get("Date"))
								))

						}
					}
					case false => {
						// TODO : 过滤f_units|f_sales和为0.0的数据
						if(f_units_sum!=0.0 && f_sales_sum!=0.0){
							if(_data_connection_cores.getCollection(company).count()==1){
								_data_connection_cores.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))
							}
							// TODO : 还原省份|城市权和索引，其他字段保持不变
							_data_connection_cores.getCollection(company).insert(Map("ID" -> MD5.md5(UUID.randomUUID().toString) ,"Provice" -> x.get("Provice"),"City" -> x.get("City"),"Panel_ID" -> x.get("Panel_ID"),"Market" -> x.get("Market"),"Product" -> x.get("Product"),"f_units" -> f_units_sum,"f_sales" -> f_sales_sum,"Date" -> x.get("Date"),"hosp_Index" -> x.get("hosp_Index"),
								"prov_Index" -> MD5.md5(x.get("Provice")+x.get("Market").toString+x.get("Product")+x.get("Date")),
								"city_Index" -> MD5.md5(x.get("Provice")+x.get("City").toString+x.get("Market")+x.get("Product")+x.get("Date"))
								))
						}
						f_units_sum2 = f_units_sum2 + f_units_sum
						f_sales_sum2 = f_sales_sum2 + f_sales_sum
						b = Some(c)
						f_units_sum = c.get("f_units").asInstanceOf[Double]
						f_sales_sum = c.get("f_sales").asInstanceOf[Double]
					}
				}
			}
		}
	}
}
