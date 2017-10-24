package module.history.HistoryData

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.Imports.{DBObject, MongoDBObject}
import com.pharbers.aqll.common.alDate.java.DateUtil
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait HistoryData {
	
	def conditions(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "yearandmonth").asOpt[String].map(x => builder += "Date" -> DateUtil.getDateLong(x)).getOrElse(Unit)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "Market" -> x).getOrElse(Unit)
		builder.result
	}
	
	implicit val d2m: DBObject => Map[String, JsValue] = { obj =>
		Map(
			"Date" -> toJson(alDateOpt.Timestamp2yyyyMM(obj.getAs[Number]("Date").get.longValue())),
			"Provice" -> toJson(obj.getAs[String]("Provice").get),
			"City" -> toJson(obj.getAs[String]("City").get),
			"Panel_ID" -> toJson(obj.getAs[String]("Panel_ID").get),
			"Market" -> toJson(obj.getAs[String]("Market").get),
			"Product" -> toJson(obj.getAs[String]("Product").get),
			"Sales" -> toJson(f"${obj.getAs[Number]("f_sales").get.doubleValue}%1.2f"),
			"Units" -> toJson(f"${obj.getAs[Number]("f_units").get.doubleValue}%1.2f")
		)
	}
}
