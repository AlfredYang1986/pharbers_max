package module.calcresult.CalcResultData

import java.util.UUID

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json._

import scala.collection.immutable.Map

trait CalcResultData {
	
	// 这个加入时间工具中，重新整理工具包的时候
	def dateRange(str: String, month: Int): Long = {
		import java.util.Calendar
		val c = Calendar.getInstance
		c.set(Calendar.YEAR, str.substring(0, 4).toInt)
		c.set(Calendar.MONTH, str.substring(4, 6).toInt - 1)
		c.set(Calendar.DATE, 1)
		c.set(Calendar.HOUR_OF_DAY, 0)
		c.set(Calendar.MINUTE, 0)
		c.set(Calendar.SECOND, 0)
		c.set(Calendar.MILLISECOND, 0)
		c.add(Calendar.MONTH, month )
		c.getTime.getTime
	}
	
	def defaultData(pr: String Map JsValue): String Map String = {
		val map = pr("result_condition").as[JsObject].value.toMap
		map("select_values").as[List[String Map String]].minBy(m => m("Date").toInt)
	}
	
	def timeRange(date: Option[String], pr: String Map JsValue): String Map Long = {
		date match {
			case None => Map("start" -> dateRange(defaultData(pr)("Date"), -12), "end" -> dateRange(defaultData(pr)("Date"), 0))
			case Some(x) => Map("start" -> dateRange(x, -12), "end" -> dateRange(x, 0))
		}
	}
	
	def shareMrHistoryBeforeCondition(data: JsValue, pr: String Map JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "date").asOpt[String].
			map(x => builder += "Date" -> DBObject("$gt" -> timeRange(Some(x), pr)("start"), "$lt" -> timeRange(Some(x), pr)("end"))).getOrElse(
				builder += "Date" -> DBObject("$gt" -> timeRange(None, pr)("start"), "$lt" -> timeRange(None, pr)("end"))
			)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "Market" -> x).getOrElse(builder += "Market" -> defaultData(pr)("Market"))
		builder.result
	}
	
	def shareMrHistoryAfterCondition(data: JsValue, pr: String Map JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "date").asOpt[String].
			map(x => builder += "_id.Date" -> DBObject("$gt" -> timeRange(Some(x), pr)("start"), "$lt" -> timeRange(Some(x), pr)("end"))).getOrElse(
				builder += "_id.Date" -> DBObject("$gt" -> timeRange(None, pr)("start"), "$lt" -> timeRange(None, pr)("end"))
			)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "_id.Market" -> x).getOrElse(builder += "_id.Market" -> defaultData(pr)("Market"))
		builder.result
	}
	
	def shareMrCurrentBeforeCondition(data: JsValue, pr: String Map JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "date").asOpt[String].
			map(x => builder += "Date" -> alDateOpt.yyyyMM2Long(x)).getOrElse(
				builder += "Date" -> alDateOpt.yyyyMM2Long(defaultData(pr)("Date"))
			)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "Market" -> x).getOrElse(builder += "Market" -> defaultData(pr)("Market"))
		builder.result
	}
	
	def shareMrCurrentAfterCondition(data: JsValue, pr: String Map JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "date").asOpt[String].
			map(x => builder += "_id.Date" -> alDateOpt.yyyyMM2Long(x)).getOrElse(
				builder += "_id.Date" -> alDateOpt.yyyyMM2Long(defaultData(pr)("Date"))
			)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "_id.Market" -> x).getOrElse(builder += "_id.Market" -> defaultData(pr)("Market"))
		builder.result
	}
	
	def areaMrHistoryBeforeCondition(data: JsValue, pr: String Map JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "date").asOpt[String].
			map(x => builder += "Date" -> alDateOpt.yyyyMM2LastLong(x)).getOrElse(
				builder += "Date" -> alDateOpt.yyyyMM2LastLong(defaultData(pr)("Date"))
			)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "Market" -> x).getOrElse(builder += "Market" -> defaultData(pr)("Market"))
		builder.result
	}
	
	def areaMrHistoryAfterCondition(data: JsValue, pr: String Map JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "date").asOpt[String].
			map(x => builder += "_id.Date" -> alDateOpt.yyyyMM2LastLong(x)).getOrElse(
				builder += "_id.Date" -> alDateOpt.yyyyMM2LastLong(defaultData(pr)("Date"))
			)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "_id.Market" -> x).getOrElse(builder += "_id.Market" -> defaultData(pr)("Market"))
		builder.result
	}
	
	def areaMrCurrentBeforeCondition(data: JsValue, pr: String Map JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "date").asOpt[String].
			map(x => builder += "Date" -> alDateOpt.yyyyMM2Long(x)).getOrElse(
				builder += "Date" -> alDateOpt.yyyyMM2Long(defaultData(pr)("Date"))
			)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "Market" -> x).getOrElse(builder += "Market" -> defaultData(pr)("Market"))
		builder.result
	}
	
	def areaMrCurrentAfterCondition(data: JsValue, pr: String Map JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "date").asOpt[String].
			map(x => builder += "_id.Date" -> alDateOpt.yyyyMM2Long(x)).getOrElse(
				builder += "_id.Date" -> alDateOpt.yyyyMM2Long(defaultData(pr)("Date"))
			)
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "_id.Market" -> x).getOrElse(builder += "_id.Market" -> defaultData(pr)("Market"))
		builder.result
	}
	
	def shareDataToMap(o: DBObject): String Map JsValue = {
		Map("Date" -> toJson(alDateOpt.Timestamp2yyyyMM(o.getAs[DBObject]("_id").get.getAs[Number]("Date").get.longValue)),
			"Product" -> toJson(o.getAs[DBObject]("_id").get.getAs[String]("Product")),
			"Sales" -> toJson(o.getAs[DBObject]("value").get.getAs[Number]("Sales").get.doubleValue.toString),
			"Units" -> toJson(o.getAs[DBObject]("value").get.getAs[Number]("Units").get.doubleValue.toString))
	}
	
	def areaDataToMap(o: DBObject): String Map JsValue = {
		Map("Date" -> toJson(alDateOpt.Timestamp2yyyyMM(o.getAs[DBObject]("_id").get.getAs[Number]("Date").get.longValue)),
			"Product" -> toJson(o.getAs[DBObject]("_id").get.getAs[String]("Product")),
			"Provinces" -> toJson(o.getAs[DBObject]("_id").get.getAs[String]("Provice")),
			"City" -> toJson(o.getAs[DBObject]("_id").get.getAs[String]("City")),
			"Sales" -> toJson(o.getAs[DBObject]("value").get.getAs[Number]("Sales").get.doubleValue.toString),
			"Units" -> toJson(o.getAs[DBObject]("value").get.getAs[Number]("Units").get.doubleValue.toString))
	}
	
	val shareMapJs: String =
		"""
		  |function() {
		  |    emit(
		  |         {"Date": this.Date,
		  |          "Market": this.Market,
		  |          "Product": this.Product
		  |         }, {"Sales": this.f_sales,
		  |             "Units": this.f_units}
		  |    );
		  |}
		""".stripMargin
	
	val shareReduceJs: String =
		"""
		  |function(key, values) {
		  |     var reducedVal = { Sales: 0, Units: 0 };
		  |     for (var i = 0; i < values.length; i++) {
		  |            reducedVal.Sales += values[i].Sales;
		  |            reducedVal.Units += values[i].Units;
		  |     }
		  |     return reducedVal;
		  |}
		""".stripMargin
	
	val areaCityMapJs: String =
		"""
		  |function() {
		  |    emit(
		  |         {"Date": this.Date,
		  |          "Market": this.Market,
		  |          "Product": this.Product,
		  |          "Provice": this.Provice,
		  |          "City": this.City
		  |         }, {"Sales": this.f_sales,
		  |             "Units": this.f_units}
		  |    );
		  |}
		""".stripMargin
	
	val areaCityReduceJs: String =
		"""
		  |function(key, values) {
		  |     var reducedVal = { Sales: 0, Units: 0 };
		  |     for (var i = 0; i < values.length; i++) {
		  |            reducedVal.Sales += values[i].Sales;
		  |            reducedVal.Units += values[i].Units;
		  |     }
		  |     return reducedVal;
		  |}
		""".stripMargin
	
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
