package com.pharbers.aqll.alCalcHelp.alFinalDataProcess

import com.mongodb.casbah.Imports.{MongoDBObject, _}
import com.pharbers.aqll.common.alDao.from
import com.pharbers.aqll.alCalcHelp.dbcores._

case class alWeightSum(uid: String, company: String, temp: String, allTable: Int) {
	
	def aggregation: Boolean = {
		try {
			val lst = (from db() in temp).selectOneByOne("hosp_Index")(x => x)
			val total = lst.size

			var b : Option[DBObject] = None
			var f_units_sum, f_sales_sum = 0.0
			var i = 0
			var flag = true

			while(lst.hasNext) {
				val c = lst.next()
				i = i + 1
				b match {
					case None =>
						b = Some(c)
						overrideSum(c, isadd = false)
						isInsertData(total == i && f_units_sum != 0.0 && f_sales_sum != 0.0, c)
					
					case Some(x) => matchHospIndex(x, c)
				}
			}

			def matchHospIndex(x: DBObject, c: DBObject) =
				if (x.get("hosp_Index") == c.get("hosp_Index")) {
					flag = true
					overrideSum(c, isadd = true)
					isInsertData(total == i && f_units_sum != 0.0 && f_sales_sum != 0.0, c)
				} else {
					if (flag) {
						flag = false
						isInsertData(f_units_sum != 0.0 && f_sales_sum != 0.0, c)
						overrideSum(c, isadd = false)
					} else {
						overrideSum(c, isadd = false)
						isInsertData(f_units_sum != 0.0 && f_sales_sum != 0.0, c)
					}
					b = Some(c)
				}
			

			def overrideSum(c: DBObject, isadd: Boolean): Unit = {
				if (isadd) {
					f_units_sum = f_units_sum + c.getAs[Number]("f_units").get.doubleValue()
					f_sales_sum = f_sales_sum + c.getAs[Number]("f_sales").get.doubleValue()
				} else {
					f_units_sum = c.getAs[Number]("f_units").get.doubleValue()
					f_sales_sum = c.getAs[Number]("f_sales").get.doubleValue()
				}
			}

			def isInsertData(isinsert: Boolean, c: DBObject) = if(isinsert) insertFinalData(c, f_units_sum, f_sales_sum)

			true
		} catch {
			case ex: Exception =>
				println(s".....>> ${ex.getMessage}")
				false
		}
	}

	private def insertFinalData(x: DBObject, f_units_sum: Double, f_sales_sum: Double) = {
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
		builder += "Provice" -> x.get("Provice")
		builder += "City" -> x.get("City")
		builder += "Panel_ID" -> x.get("Panel_ID")
		builder += "Market" -> x.get("Market")
		builder += "Product" ->  x.get("Product")
		builder += "f_units" -> f_units_sum
		builder += "f_sales" -> f_sales_sum
		builder += "Date" -> x.get("Date")
		builder += "prov_Index" -> s"${x.get("Provice")}${x.get("Market")}${x.get("Product")}${x.get("Date")}".hashCode//alEncryptionOpt.md5(s"${x.get("Provice")}${x.get("Market")}${x.get("Product")}${x.get("Date")}")
		builder += "city_Index" -> s"${x.get("Provice")}${x.get("City")}${x.get("Market")}${x.get("Product")}${x.get("Date")}".hashCode//alEncryptionOpt.md5(s"${x.get("Provice")}${x.get("City")}${x.get("Market")}${x.get("Product")}${x.get("Date")}")
		builder += "hosp_Index" -> x.get("hosp_Index")
		dbc.getCollection(company) += builder.result()
	}
}
