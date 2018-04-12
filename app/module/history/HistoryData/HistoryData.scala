package module.history.HistoryData

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports.{DBObject, MongoDBObject}

import com.pharbers.common.datatype.date.{DateUtil, PhDateOpt}

trait HistoryData {
	
//	class DataType(n: Int, s: String)
//	case class Provinces() extends DataType(0, "provinces")
//	case class City() extends DataType(1, "city")
//	case class Hospital() extends DataType(2, "hospital")
	
	def conditions(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "date").asOpt[String].map{x =>
			val str = x.filter(_ != ' ')
			val head = str.split("~").head
			val last = str.split("~").tail.head
			val start = head.split("-").head + head.split("-").tail.head
			val end = last.split("-").head + last.split("-").tail.head
			builder += "Date" -> MongoDBObject("$gte" -> DateUtil.getDateLong(start),"$lt" -> DateUtil.getDateLong(end))
		}.getOrElse(Unit)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "Market" -> x).getOrElse(Unit)
		builder.result
	}
	
	def aggregateHistorySelectResult(x: DBObject): String Map JsValue = {
		val status = x.getAs[Number]("ok").get.intValue()
		if (status == 0) throw new Exception("db aggregation error")
		else {
			val result = x.getAs[MongoDBList]("result").get
			val list = result.toList.asInstanceOf[List[DBObject]]
			val r = list.map { x =>
				val reVal = x.getAs[DBObject]("_id").get
				reVal.getAs[String]("Market").get
			}
			Map("market" -> toJson(r))
		}
	}

	implicit val d2m: DBObject => Map[String, JsValue] = { obj =>
		Map(
			"Date" -> toJson(PhDateOpt.Timestamp2yyyyMM(obj.getAs[Number]("Date").get.longValue())),
			"Provice" -> toJson(obj.getAs[String]("Provice").get),
			"City" -> toJson(obj.getAs[String]("City").get),
			"Panel_ID" -> toJson(obj.getAs[String]("Panel_ID").get),
			"Market" -> toJson(obj.getAs[String]("Market").get),
			"Product" -> toJson(obj.getAs[String]("Product").get),
			"Sales" -> toJson(f"${obj.getAs[Number]("f_sales").get.doubleValue}%1.2f"),
			"Units" -> toJson(f"${obj.getAs[Number]("f_units").get.doubleValue}%1.2f")
		)
	}
	
	def tableOutHtml(data: JsValue)(implicit lst: List[String Map JsValue]): play.twirl.api.HtmlFormat.Appendable = {
		(data \ "condition" \ "datatype").asOpt[String] match {
			case None => views.html.calcPages.hsitory.historyByProvincesTable(lst)
			case Some(x) => x match {
				case "provinces" => views.html.calcPages.hsitory.historyByProvincesTable(lst)
				case "city" => views.html.calcPages.hsitory.historyByCityTable(lst)
				case "hospital" => views.html.calcPages.hsitory.historyByHospitalTable(lst)
			}
		}
	}
}
