package com.pharbers.aqll.alcalc.alfinaldataprocess

import java.util.UUID
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.calc.util.dao.{_data_connection, from}
import com.pharbers.aqll.calc.util.MD5
/**
  * Created by LIWEI on 17-3-17.
  */
object alWeightSum extends App{
	def apply(company : String): alWeightSum = new alWeightSum(company)
}

class alWeightSum(company : String){
	val lst = (from db() in company+"_temp").selectOneByOne("Index")(x => x)
	var b : Option[DBObject] = None
	var f_units_sum,f_sales_sum = 0.0
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
				if(total==i && f_units_sum!=0.0 && f_sales_sum!=0.0){
					if(_data_connection.getCollection(company).count()==1){
						_data_connection.getCollection(company).createIndex(MongoDBObject("Index" -> 1))
					}
					_data_connection.getCollection(company).insert(Map("ID" -> MD5.md5(UUID.randomUUID().toString) ,"Provice" -> c.get("Provice"),"City" -> c.get("City"),"Panel_ID" -> c.get("Panel_ID"),"Market" -> c.get("Market"),"Product" -> c.get("Product"),"f_units" -> f_units_sum,"f_sales" -> f_sales_sum,"Date" -> c.get("Date"),"Index" -> c.get("Index")))
				}
			}
			case Some(x) => {
				var flag = x.get("Index").equals(c.get("Index"))
				flag match {
					case true => {
						f_units_sum = f_units_sum + c.get("f_units").asInstanceOf[Double]
						f_sales_sum = f_sales_sum + c.get("f_sales").asInstanceOf[Double]
						if(total==i && f_units_sum!=0.0 && f_sales_sum!=0.0){
							if(_data_connection.getCollection(company).count()==1){
								_data_connection.getCollection(company).createIndex(MongoDBObject("Index" -> 1))
							}
							_data_connection.getCollection(company).insert(Map("ID" -> MD5.md5(UUID.randomUUID().toString) ,"Provice" -> x.get("Provice"),"City" -> x.get("City"),"Panel_ID" -> x.get("Panel_ID"),"Market" -> x.get("Market"),"Product" -> x.get("Product"),"f_units" -> f_units_sum,"f_sales" -> f_sales_sum,"Date" -> x.get("Date"),"Index" -> x.get("Index")))

						}
					}
					case false => {
						if(f_units_sum!=0.0 && f_sales_sum!=0.0){
							if(_data_connection.getCollection(company).count()==1){
								_data_connection.getCollection(company).createIndex(MongoDBObject("Index" -> 1))
							}
							_data_connection.getCollection(company).insert(Map("ID" -> MD5.md5(UUID.randomUUID().toString) ,"Provice" -> x.get("Provice"),"City" -> x.get("City"),"Panel_ID" -> x.get("Panel_ID"),"Market" -> x.get("Market"),"Product" -> x.get("Product"),"f_units" -> f_units_sum,"f_sales" -> f_sales_sum,"Date" -> x.get("Date"),"Index" -> x.get("Index")))
						}
						b = Some(c)
						f_units_sum = c.get("f_units").asInstanceOf[Double]
						f_sales_sum = c.get("f_sales").asInstanceOf[Double]
					}
				}
			}
		}
	}
}
