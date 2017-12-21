package com.pharbers.aqll.alCalcOther.alfinaldataprocess

import java.util.UUID

import com.mongodb.casbah.Imports.{MongoDBObject, _}
import com.pharbers.aqll.common.alDao.from
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.alCalcHelp.dbcores._

/**
  * Created by liwei on 17-3-17.
  */
case class alWeightSum() {

	def apply(company : String, u: String): JsValue = {
		try {
			val lst = (from db() in u).selectOneByOne("hosp_Index")(x => x)
			var b : Option[DBObject] = None
			var f_units_sum, f_sales_sum = 0.0
			var i = 0
			var flag = true
			val total = lst.size
			while(lst.hasNext) {
				val c : DBObject = lst.next()
				i = i+1
				b match {
					case None => {
						b = Some(c)
						overrideSum(c,false)
						isInsertData((total == i && f_units_sum != 0.0 && f_sales_sum != 0.0), c)
					}
					case Some(x) => matchHospIndex(x, c)
				}
			}

			def matchHospIndex(x: DBObject, c: DBObject) = x.get("hosp_Index") == c.get("hosp_Index") match {
				case true => {
					flag = true
					overrideSum(c, true)
					isInsertData((total == i && f_units_sum != 0.0 && f_sales_sum != 0.0), c)
				}
				case false => {
					if(flag) {
						flag = false
						isInsertData((f_units_sum != 0.0 && f_sales_sum != 0.0), c)
						overrideSum(c, false)
					}else {
						overrideSum(c, false)
						isInsertData((f_units_sum != 0.0 && f_sales_sum != 0.0), c)
					}
					b = Some(c)
				}
			}

			def overrideSum(c: DBObject,isadd: Boolean) = isadd match {
				case true => {
					f_units_sum = f_units_sum + c.get("f_units").asInstanceOf[Double]
					f_sales_sum = f_sales_sum + c.get("f_sales").asInstanceOf[Double]
				}
				case false => {
					f_units_sum = c.get("f_units").asInstanceOf[Double]
					f_sales_sum = c.get("f_sales").asInstanceOf[Double]
				}
			}

			def isInsertData(isinsert: Boolean, c: DBObject) = if(isinsert) insertFinalData(c, company, f_units_sum, f_sales_sum)

			def insertFinalData(x: DBObject,company: String, f_units_sum: Double, f_sales_sum: Double) = {
				if(dbc.getCollection(company).count() == 1){
					dbc.getCollection(company).createIndex(MongoDBObject("hosp_Index" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("prov_Index" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("city_Index" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("Market" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1))
					dbc.getCollection(company).createIndex(MongoDBObject("Date" -> 1,"Market" -> 1,"City" -> 1))
				}

				val builder = MongoDBObject.newBuilder
				builder += "ID" -> alEncryptionOpt.md5(UUID.randomUUID().toString)
				builder += "Provice" -> x.get("Provice")
				builder += "City" -> x.get("City")
				builder += "Panel_ID" -> x.get("Panel_ID")
				builder += "Market" -> x.get("Market")
				builder += "Product" ->  x.get("Product")
				builder += "f_units" -> f_units_sum
				builder += "f_sales" -> f_sales_sum
				builder += "Date" -> x.get("Date")
				builder += "prov_Index" -> alEncryptionOpt.md5(s"${x.get("Provice")}${x.get("Market")}${x.get("Product")}${x.get("Date")}")
				builder += "city_Index" -> alEncryptionOpt.md5(s"${x.get("Provice")}${x.get("City")}${x.get("Market")}${x.get("Product")}${x.get("Date")}")
				builder += "hosp_Index" -> x.get("hosp_Index")
				dbc.getCollection(company) += builder.result()
			}

			toJson(successToJson())
		} catch {
			case ex: Exception =>
				println(s".....>> ${ex}")
				errorToJson(ex.getMessage)
		}
	}
}