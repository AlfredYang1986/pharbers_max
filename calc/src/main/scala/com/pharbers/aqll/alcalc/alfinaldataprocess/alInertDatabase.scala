package com.pharbers.aqll.alcalc.alfinaldataprocess

/**
  * Created by LIWEI on 2017/3/15.
  */

import java.util.UUID
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.calc.excel.model.westMedicineIncome
import com.pharbers.aqll.calc.util.MD5
import com.pharbers.aqll.calc.util.dao._data_connection

object alInertDatabase {
	def apply(mrd : westMedicineIncome,sub_uuid : String): alInertDatabase = new alInertDatabase(mrd,sub_uuid)
}

class alInertDatabase(mrd : westMedicineIncome,sub_uuid : String) {
	_data_connection.getCollection(sub_uuid).insert(
		Map("ID" -> MD5.md5(UUID.randomUUID().toString),
			"Provice" -> mrd.province,
			"City" -> mrd.prefecture,
			"Panel_ID" -> mrd.phaid,
			"Market" -> mrd.market1Ch,
			"Product" -> mrd.minimumUnitCh,
			"f_units" -> mrd.finalResultsUnit,
			"f_sales" -> mrd.finalResultsValue,
			"Date" -> mrd.yearAndmonth.asInstanceOf[Integer],
			"prov_Index" -> MD5.md5(mrd.province+mrd.market1Ch+mrd.minimumUnitCh+mrd.yearAndmonth.asInstanceOf[Integer]),
			"city_Index" -> MD5.md5(mrd.province+mrd.prefecture+mrd.market1Ch+mrd.minimumUnitCh+mrd.yearAndmonth.asInstanceOf[Integer]),
			"hosp_Index" -> MD5.md5(mrd.province+mrd.prefecture+mrd.phaid+mrd.market1Ch+mrd.minimumUnitCh+mrd.yearAndmonth.asInstanceOf[Integer])
		)
	)
}