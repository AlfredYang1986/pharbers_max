package module

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, CommonModule, MessageDefines, ModuleTrait}
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import module.common.alRestDate
import module.common.alSampleCheck
import com.pharbers.aqll.common.alDao.data_connection
import scala.collection.mutable.ListBuffer
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

object SampleCheckModuleMessage {
	sealed class msg_CheckBaseQuery extends CommonMessage
	case class msg_samplecheck(data: JsValue) extends msg_CheckBaseQuery
}

object SampleCheckModule extends ModuleTrait {
	import SampleCheckModuleMessage._
	import controllers.common.default_error_handler.f

	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm : CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_samplecheck(data) => msg_check_func(data)
	}

	/**
		* @author liwei
		* @param data
		* @return
		*/
	def msg_check_func(data: JsValue)(implicit error_handler: Int => JsValue, cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val database = cm.modules.get.get("db").get.asInstanceOf[data_connection]
		val company = (data \ "company").asOpt[String].getOrElse("")
		val market = (data \ "market").asOpt[String].getOrElse("")
		val date = (data \ "date").asOpt[String].getOrElse("")
		try {

			val cur_date = alDateOpt.MMyyyy2yyyyMM(date)
			val las_date = alDateOpt.Timestamp2yyyyMM(alDateOpt.MMyyyy2LastLong(date))
			val cur12_date = alSampleCheck.matchThisYearData(alRestDate.diff12Month(cur_date),queryNear12(database,company,market,date))
			val las12_date = alSampleCheck.matchLastYearData(alRestDate.diff12Month(las_date),queryLast12(database,company,market,date))
			val cur_data = query_cel_data(database,query(company,market,date,"cur"))
			val ear_data = query_cel_data(database,query(company,market,date,"ear"))
			val las_data = query_cel_data(database,query(company,market,date,"las"))
			val mismatch_lst = misMatchHospital(database,query(company,market,date,"cur"));
			(successToJson(toJson(Map(
				"cur_data" -> cur_data,
				"ear_data" -> ear_data,
				"las_data" -> las_data,
				"cur12_date" -> alSampleCheck.lst2Json(cur12_date),
				"las12_date" -> alSampleCheck.lst2Json(las12_date),
				"misMatchHospital" -> mismatch_lst
			))), None)
		} catch {
			case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
		}
	}

	/**
		* @author liwei
		* @param query
		* @return
		*/
	def query_cel_data(database: data_connection,query: DBObject): JsValue ={
		val data = database.getCollection("FactResult").find(query)
		var hospNum,productNum,marketNum = 0
		var sales,units = 0.0
		var date = ""
		while (data.hasNext) {
			val obj = data.next()
			hospNum = obj.get("HospNum").asInstanceOf[Number].intValue()
			productNum = obj.get("ProductNum").asInstanceOf[Number].intValue()
			marketNum = obj.get("MarketNum").asInstanceOf[Number].intValue()
			sales = obj.get("Sales").asInstanceOf[Number].doubleValue()
			units = obj.get("Units").asInstanceOf[Number].doubleValue()
			date = alDateOpt.Timestamp2yyyyMM(obj.get("Date").asInstanceOf[Number].longValue())
		}
		toJson(Map("HospNum" -> toJson(hospNum),"ProductNum" -> toJson(productNum),"MarketNum" -> toJson(marketNum),"Sales" -> toJson(sales),"Units" -> toJson(units),"Date" -> toJson(date)))
	}

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @param query_type
		* @return
		*/
	def query(company: String,market: String,date: String,query_type: String): DBObject = query_type match {
		case "cur" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> alDateOpt.MMyyyy2Long(date)))
		case "ear" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> alDateOpt.MMyyyy2EarlyLong(date)))
		case "las" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> alDateOpt.MMyyyy2LastLong(date)))
	}

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @return
		*/
	def queryNear12(database: data_connection,company: String,market: String,date: String): List[List[Map[String,AnyRef]]] = {
		val cur_date = alDateOpt.MMyyyy2yyyyMM(date)
		val date_lst = alDateOpt.ArrayDate2ArrayTimeStamp(alRestDate.diff12Month(cur_date))
		val query = MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$in" -> date_lst))
		val f_lst = database.getCollection("FactResult").find(query).sort(MongoDBObject("Date" -> 1))
		val s_lst = database.getCollection("SampleCheckResult").find(query).sort(MongoDBObject("Date" -> 1))
		val factResult = f_lst.map{x =>
			Map(
				"Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
				"HospNum" -> x.get("HospNum"),"ProductNum" -> x.get("ProductNum"),"MarketNum" -> x.get("MarketNum"),"Sales" -> x.get("Sales"),"Units" -> x.get("Units"))
		} toList

		val sampleCheck = s_lst.map{x =>
			Map(
				"Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
				"HospNum" -> x.get("HospNum"),"ProductNum" -> x.get("ProductNum"),"MarketNum" -> x.get("MarketNum"),"Sales" -> x.get("Sales"),"Units" -> x.get("Units"))
		} toList

		List(factResult,sampleCheck)
	}

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @return
		*/
	def queryLast12(database: data_connection,company: String,market: String,date: String): List[Map[String,AnyRef]] = {
		val las_date = alDateOpt.MMyyyy2LastLong(date)
		val cur_date = alDateOpt.Timestamp2yyyyMM(las_date)
		val date_lst = alDateOpt.ArrayDate2ArrayTimeStamp(alRestDate.diff12Month(cur_date))
		val query = MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$in" -> date_lst))
		val lst = database.getCollection("SampleCheckResult").find(query).sort(MongoDBObject("Date" -> 1))
		lst map{ x =>
			Map(
				"Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
				"HospNum" -> x.get("HospNum"),"ProductNum" -> x.get("ProductNum"),"MarketNum" -> x.get("MarketNum"),"Sales" -> x.get("Sales"),"Units" -> x.get("Units"))
		} toList
	}

	/**
		* @author liwei
		* @param query
		* @return
		*/
	def misMatchHospital(database: data_connection,query: DBObject): JsValue ={
		val data = database.getCollection("FactResult").find(query)
		val Mismatch = new ListBuffer[JsValue]()
		while (data.hasNext) {
			val obj = data.next()
			obj.get("Mismatch").asInstanceOf[BasicDBList].foreach{ x =>
				val obj = x.asInstanceOf[BasicDBObject]
				Mismatch.append(toJson(Map(
					"Hosp_name" -> toJson(obj.get("Hosp_name").asInstanceOf[String]),
					"Province" -> toJson(obj.get("Province").asInstanceOf[String]),
					"City" -> toJson(obj.get("City").asInstanceOf[String]),
					"City_level" -> toJson(obj.get("City_level").asInstanceOf[String])
				)))
			}
		}
		toJson(Mismatch)
	}
}