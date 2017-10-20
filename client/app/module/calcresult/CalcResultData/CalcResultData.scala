package module.calcresult.CalcResultData

import java.util.UUID

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import play.api.libs.json.JsValue
import play.api.libs.json.Json._

import scala.collection.immutable.Map

trait CalcResultData {
	
	def aggregateSalesResult(x: DBObject): String Map JsValue = {
		val status = x.getAs[Number]("ok").get.intValue()
		if(status == 0) throw new Exception("db aggregation error")
		else {
			val result = x.getAs[MongoDBList]("result").get
			val list = result.toList.asInstanceOf[List[DBObject]]
			list.find(x => x.getAs[DBObject]("_id") != None).map { z =>
				val reVal = z.getAs[DBObject]("_id").get
				val map = Map("date" -> toJson(alDateOpt.Timestamp2yyyyMM(reVal.getAs[Number]("Date").get.longValue())), "market" -> toJson(reVal.getAs[String]("Market").get))
				Map(UUID.randomUUID().toString -> toJson(map))
			}.getOrElse(throw new Exception(""))
		}
	}
}
