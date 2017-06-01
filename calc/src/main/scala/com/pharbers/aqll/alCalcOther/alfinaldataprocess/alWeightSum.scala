package com.pharbers.aqll.alCalcOther.alfinaldataprocess

import java.util.UUID

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.common.alDao.from
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.alCalaHelp.dbcores._

/**
	* Created by liwei on 17-3-17.
	*/
case class alWeightSum() {

	def apply(company : String, u: String): JsValue = {
		try {
			val lst = (from db() in u).selectOneByOne("hosp_Index")(x => x)
			var b : Option[DBObject] = None
			var f_units_sum,f_sales_sum,f_units_sum2,f_sales_sum2 = 0.0
			var i = 0
			val total = lst.size
			while(lst.hasNext) {
				val c : DBObject = lst.next()
				i = i+1
				b match {
					case None => {
						b = Some(c)
						overrideSum(c,false)
						isInsertData((total==i && f_units_sum!=0.0 && f_sales_sum!=0.0),c)
					}
					case Some(x) => matchHospIndex(x,c)
				}
			}

			def matchHospIndex(x: DBObject,c: DBObject): Unit = x.get("hosp_Index").equals(c.get("hosp_Index")) match {
				case true => {
					overrideSum(c,true)
					isInsertData((total==i && f_units_sum!=0.0 && f_sales_sum!=0.0),c)
				}
				case false => {
					isInsertData((f_units_sum!=0.0 && f_sales_sum!=0.0),c)
					f_units_sum2 = f_units_sum2 + f_units_sum
					f_sales_sum2 = f_sales_sum2 + f_sales_sum
					b = Some(c)
					overrideSum(c,false)
				}
			}

			def overrideSum(c: DBObject,isadd: Boolean): Unit = isadd match {
				case true => {
					f_units_sum = f_units_sum + c.get("f_units").asInstanceOf[Double]
					f_sales_sum = f_sales_sum + c.get("f_sales").asInstanceOf[Double]
				}
				case false => {
					f_units_sum = c.get("f_units").asInstanceOf[Double]
					f_sales_sum = c.get("f_sales").asInstanceOf[Double]
				}
			}

			def isInsertData(isinsert: Boolean,c: DBObject) {
				if(isinsert) insertFinalData(c,company,f_units_sum,f_sales_sum)
			}

			def insertFinalData(x: DBObject,company: String,f_units_sum: Double,f_sales_sum: Double) {
				if(dbc.getCollection(company).count()==1){
					dbc.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("prov_Index" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("city_Index" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("Market" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1,"City" -> 1))
				}

				val Provice = x.get("Provice")
				val City = x.get("City")
				val Market = x.get("Market")
				val Product = x.get("Product")
				val Date = x.get("Date")

				dbc.getCollection(company).insert(MongoDBObject(
					"ID" -> alEncryptionOpt.md5(UUID.randomUUID().toString) ,
					"Provice" -> Provice,
					"City" -> City,
					"Panel_ID" -> x.get("Panel_ID"),
					"Market" -> Market,
					"Product" -> Product,
					"f_units" -> f_units_sum,
					"f_sales" -> f_sales_sum,
					"Date" -> Date,
					"hosp_Index" -> x.get("hosp_Index"),
					"prov_Index" -> alEncryptionOpt.md5(s"${Provice}${Market}${Product}${Date}"),
					"city_Index" -> alEncryptionOpt.md5(s"${Provice}${City}${Market}${Product}${Date}")
				))
			}

			toJson(successToJson())
		} catch {
			case ex: Exception => errorToJson(ex.getMessage)
		}
	}
}