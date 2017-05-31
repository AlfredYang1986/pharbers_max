package com.pharbers.aqll.alCalcOther.alfinaldataprocess

/**
  * Created by liwei on 2017/3/15.
  */

import java.util.UUID

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.alCalc.almodel.scala.westMedicineIncome
import com.pharbers.aqll.common.alDao._data_connection_cores_thread
import com.pharbers.aqll.common.alDate.java.DateUtil
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

case class alInertDatabase(){
	def apply(mrd : westMedicineIncome,sub_uuid : String): JsValue = {
		try {
			val builder = MongoDBObject.newBuilder
			builder += "ID" -> alEncryptionOpt.md5(UUID.randomUUID().toString)
			builder += "Provice" -> mrd.getV("province").toString
			builder += "City" -> mrd.getV("prefecture").toString
			builder += "Panel_ID" -> mrd.phaid
			builder += "Market" -> mrd.getV("market1Ch").toString
			builder += "Product" ->  mrd.minimumUnitCh
			builder += "f_units" -> mrd.finalResultsUnit
			builder += "f_sales" -> mrd.finalResultsValue
			builder += "Date" -> DateUtil.getDateLong(mrd.yearAndmonth.toString)
			builder += "prov_Index" -> alEncryptionOpt.md5(mrd.getV("province").toString+mrd.getV("market1Ch").toString+mrd.minimumUnitCh+mrd.yearAndmonth)
			builder += "city_Index" -> alEncryptionOpt.md5(mrd.getV("province").toString+mrd.getV("prefecture").toString+mrd.getV("market1Ch").toString+mrd.minimumUnitCh+mrd.yearAndmonth)
			builder += "hosp_Index" -> alEncryptionOpt.md5(mrd.getV("province").toString+mrd.getV("prefecture").toString+mrd.phaid+mrd.getV("market1Ch").toString+mrd.minimumUnitCh+mrd.yearAndmonth)
			_data_connection_cores_thread.getCollection(sub_uuid) += builder.result()
			toJson(successToJson())
		} catch {
			case ex: Exception => errorToJson(ex.getMessage)
		}
	}
}