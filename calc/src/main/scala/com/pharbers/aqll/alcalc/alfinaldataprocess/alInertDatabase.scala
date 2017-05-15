package com.pharbers.aqll.alcalc.alfinaldataprocess

/**
  * Created by LIWEI on 2017/3/15.
  */

import java.util.UUID

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.alcalc.almodel.westMedicineIncome
import com.pharbers.aqll.util.dao._data_connection_thread
import com.pharbers.aqll.util.DateUtil

object alInertDatabase {
	def apply(mrd : westMedicineIncome,sub_uuid : String): alInertDatabase = new alInertDatabase(mrd,sub_uuid)
}

class alInertDatabase(mrd : westMedicineIncome,sub_uuid : String) {
	try {
		val builder = MongoDBObject.newBuilder
		builder += "ID" -> MD5.md5(UUID.randomUUID().toString)
		builder += "Provice" -> mrd.getV("province").toString
		builder += "City" -> mrd.getV("prefecture").toString
		builder += "Panel_ID" -> mrd.phaid
		builder += "Market" -> mrd.getV("market1Ch").toString
		builder += "Product" ->  mrd.minimumUnitCh
		builder += "f_units" -> mrd.finalResultsUnit
		builder += "f_sales" -> mrd.finalResultsValue
		builder += "Date" -> DateUtil.getDateLong(mrd.yearAndmonth.toString)
		builder += "prov_Index" -> MD5.md5(mrd.getV("province").toString+mrd.getV("market1Ch").toString+mrd.minimumUnitCh+mrd.yearAndmonth)
		builder += "city_Index" -> MD5.md5(mrd.getV("province").toString+mrd.getV("prefecture").toString+mrd.getV("market1Ch").toString+mrd.minimumUnitCh+mrd.yearAndmonth)
		builder += "hosp_Index" -> MD5.md5(mrd.getV("province").toString+mrd.getV("prefecture").toString+mrd.phaid+mrd.getV("market1Ch").toString+mrd.minimumUnitCh+mrd.yearAndmonth)
		_data_connection_thread.getCollection(sub_uuid) += builder.result()
	} catch {
		case ex: java.util.NoSuchElementException =>
			println(s"funcking $sub_uuid \n ex = ${ex}")
	}

}