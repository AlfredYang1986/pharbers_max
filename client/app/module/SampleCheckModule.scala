package module

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.dao.{_data_connection_cores, from}
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import com.pharbers.aqll.util.DateUtils
import scala.collection.mutable.ListBuffer

object SampleCheckModuleMessage {
	sealed class msg_CheckBaseQuery extends CommonMessage
	case class msg_samplecheck(data: JsValue) extends msg_CheckBaseQuery
}

object SampleCheckModule extends ModuleTrait {

	import SampleCheckModuleMessage._
	import controllers.common.default_error_handler.f

	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_samplecheck(data) => msg_check_func(data)
		case _ => println("Error---------------"); ???
	}

	def msg_check_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val company = (data \ "company").asOpt[String].getOrElse("")
		val market = (data \ "market").asOpt[String].getOrElse("")
		val date = (data \ "date").asOpt[String].getOrElse("")

		val top_query_mismatch = MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2Long(date)))
		val top_query_early = MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2EarlyLong(date)))
		val top_query_last = MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2LastLong(date)))

		val early12_date = DateUtils.MMyyyy2Early12Long(date)
		val top_query_early12 = MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$gte" -> early12_date.head,"$lt" -> early12_date.tail.head))

		val last12_date = DateUtils.MMyyyy2Last12Long(date)
		val top_query_last12 = MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$gte" -> last12_date.head,"$lt" -> last12_date.tail.head))

		try {
			val top_current_mismatch = (from db() in "SampleCheck" where top_query_mismatch).select(Top_Current_Mismatch(_))(_data_connection_cores).toList
			val top_early = (from db() in "SampleCheck" where top_query_early).select(Top_Early_Last(_))(_data_connection_cores).toList
			val top_last = (from db() in "SampleCheck" where top_query_last).select(Top_Early_Last(_))(_data_connection_cores).toList
			val top_early12 = (from db() in "SampleCheck" where top_query_early12).selectSort("Date")(Top_Early12_Last12(_))(_data_connection_cores).toList
			val top_last12 = (from db() in "SampleCheck" where top_query_last12).selectSort("Date")(Top_Early12_Last12(_))(_data_connection_cores).toList

			(Some(Map(
				"top_mismatch" -> toJson(top_current_mismatch),
				"top_early" -> toJson(top_early),
				"top_last" -> toJson(top_last),
				"top_early12" -> toJson(top_early12),
				"top_last12" -> toJson(top_last12)
			)), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def Top_Current_Mismatch(d: MongoDBObject): JsValue = {
		val HospNum = d.getAs[Number]("HospNum").get.longValue()
		val ProductNum = d.getAs[Number]("ProductNum").get.longValue()
		val MarketNum = d.getAs[Number]("MarketNum").get.longValue()
		val mismatch = d.getAs[MongoDBList]("Mismatch").get.toList.asInstanceOf[List[BasicDBObject]]
		val lst = ListBuffer[JsValue]()
		mismatch.foreach{x =>
			lst.append(toJson(Map(
				"Hosp_name" -> toJson(x.getAs[String]("Hosp_name").get),
				"Province" -> toJson(x.getAs[String]("Province").get),
				"City" -> toJson(x.getAs[String]("City").get),
				"City_level" -> toJson(x.getAs[String]("City_level").get)
			)))
		}
		println(lst.size)
		toJson(Map("HospNum" -> toJson(HospNum),"ProductNum" -> toJson(ProductNum),"MarketNum" -> toJson(MarketNum),"MisMatch" -> toJson(lst.toList)))
	}

	def Top_Early_Last(d: MongoDBObject): JsValue = {
		val HospNum = d.getAs[Number]("HospNum").get.longValue()
		val ProductNum = d.getAs[Number]("ProductNum").get.longValue()
		val MarketNum = d.getAs[Number]("MarketNum").get.longValue()
		toJson(Map("HospNum" -> toJson(HospNum),"ProductNum" -> toJson(ProductNum),"MarketNum" -> toJson(MarketNum)))
	}

	def Top_Early12_Last12(d: MongoDBObject): JsValue = {
		val Sales = d.getAs[Number]("Sales").get.doubleValue()
		val Units = d.getAs[Number]("Units").get.doubleValue()
		val date = DateUtils.Timestamp2yyyyMM(d.getAs[Number]("Date").get.longValue())
		toJson(Map("Sales" -> toJson(Sales),"Units" -> toJson(Units),"Date" -> toJson(date)))
	}
}