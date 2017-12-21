package module.calcresult.CalcResultData

import java.util.UUID

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import play.api.libs.json.JsValue
import play.api.libs.json.Json._

import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer

trait CalcResultData {
	
	def aggregateConditionResult(x: DBObject): String Map JsValue = {
		val status = x.getAs[Number]("ok").get.intValue()
		if(status == 0) throw new Exception("db aggregation error")
		else {
			val result = x.getAs[MongoDBList]("result").get
			val list = result.toList.asInstanceOf[List[DBObject]]
			val map = list.filter(x => x.getAs[DBObject]("_id").isDefined).map { z =>
				val reVal = z.getAs[DBObject]("_id").getOrElse(throw new Exception(""))
				Map("Date" -> toJson(alDateOpt.Timestamp2yyyyMM(reVal.getAs[Number]("Date").get.longValue())), "Market" -> toJson(reVal.getAs[String]("Market").get))
			}
			Map(UUID.randomUUID().toString -> toJson(map))
		}
	}
	
	def aggregateSalesResult(x: DBObject)(id: String): String Map JsValue = {
		val status = x.getAs[Number]("ok").get.intValue()
		if(status == 0) throw new Exception("db aggregation error")
		else {
			val result = x.getAs[MongoDBList]("result").get
			val list = result.toList.asInstanceOf[List[DBObject]]
			val r = list.map{ x =>
				val reVal = x.getAs[DBObject]("_id").get
				val sales = x.getAs[Number]("Sales").get.doubleValue().toString
				val units = x.getAs[Number]("Units").get.doubleValue().toString
				val date = alDateOpt.Timestamp2yyyyMM(reVal.getAs[Number]("Date").map(x => x.longValue()).getOrElse(0))
				val market = reVal.getAs[String]("Market").getOrElse("")
				val product = reVal.getAs[String]("Product").getOrElse("")
				val province = reVal.getAs[String]("Provice").getOrElse("")
				Map("Date" -> toJson(date),
					"Market" -> toJson(market),
					"Product" -> toJson(product),
					"Province" -> toJson(province),
					"Sales" -> toJson(sales),
					"Units" -> toJson(units))
			}
			Map(id -> toJson(r))
		}
	}
}
