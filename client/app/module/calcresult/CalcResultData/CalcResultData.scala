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
			list.find(x => x.getAs[DBObject]("_id") != None).map { z =>
				val reVal = z.getAs[DBObject]("_id").get
				val map = Map("Date" -> toJson(alDateOpt.Timestamp2yyyyMM(reVal.getAs[Number]("Date").get.longValue())), "Market" -> toJson(reVal.getAs[String]("Market").get))
				Map(UUID.randomUUID().toString -> toJson(map))
			}.getOrElse(throw new Exception(""))
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
				val sales = x.getAs[Number]("Sales").get.doubleValue()
				val units = x.getAs[Number]("Units").get.doubleValue()
				val date = alDateOpt.Timestamp2yyyyMM(reVal.getAs[Number]("Date").map(x => x.longValue()).getOrElse(0))
				val market = reVal.getAs[String]("Market").get
				val city = reVal.getAs[String]("City").getOrElse("")
				Map("Date" -> toJson(date),
					"Market" -> toJson(market),
					"City" -> toJson(city),
					"Sales" -> toJson(sales),
					"Units" -> toJson(units))
				
			}
			
			Map(id -> toJson(r))
		}
	}
}

object alNearDecemberMonth {
	
	def diff12Month(date: String): Array[String] = {
		val year = date.substring(0, 4).toInt
		val month = date.substring(4, date.length).toInt
		val temp = new ArrayBuffer[String]()
		val lst = diffDate(year, month, (year.toInt - 1), month)(temp)
		lst.sortBy(x => x)
	}
	
	def diffDate(cur_year: Int, cur_month: Int, ear_year: Int, ear_month: Int)(temp: ArrayBuffer[String]): Array[String] = {
		(ear_year, ear_month) match {
			case (x, y) if x.equals(cur_year) && y.equals(cur_month) => temp.toArray
			case _ => {
				ear_month match {
					case i if i >= 12 => {
						temp += s"$cur_year${diffMonth(s"${i + 1 - 12}")}"
						diffDate(cur_year, cur_month, cur_year, i + 1 - 12)(temp)
					}
					case _ => {
						temp += s"$ear_year${diffMonth(s"${ear_month + 1}")}"
						diffDate(cur_year, cur_month, ear_year, ear_month + 1)(temp)
					}
				}
			}
		}
	}
	
	def diffMonth(month: String): String = month.length match {
		case 1 => 0 + month
		case _ => month
	}
}
