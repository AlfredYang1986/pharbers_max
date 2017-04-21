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
	}

	/**
		* @author liwei
		* @param data
		* @param error_handler
		* @return
		*/
	def msg_check_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val company = (data \ "company").asOpt[String].getOrElse("")
		val market = (data \ "market").asOpt[String].getOrElse("")
		val date = (data \ "date").asOpt[String].getOrElse("")
		try {
			val cur_data = query_cel_data(query(company,market,date,"cur"))
			val ear_data = query_cel_data(query(company,market,date,"ear"))
			val las_data = query_cel_data(query(company,market,date,"las"))
			val cur_query12 = query(company,market,date,"cur12")
			val cur12_data_H = HPM_Near12(date,cur_query12,"H")(cur_data)
			val cur12_data_P = HPM_Near12(date,cur_query12,"P")(cur_data)
			val cur12_data_M = HPM_Near12(date,cur_query12,"M")(cur_data)
			val las_query12 = query(company,market,date,"las12")
			val cur12_las12_data = SU_Near12(date,cur_query12,las_query12)(cur_data)
			val mismatch_lst = misMatchHospital(query(company,market,date,"cur"));

			(Some(Map(
				"cur_data" -> cur_data,
				"ear_data" -> ear_data,
				"las_data" -> las_data,
				"cur12_data_H" -> cur12_data_H,
				"cur12_data_P" -> cur12_data_P,
				"cur12_data_M" -> cur12_data_M,
				"cur12_las12_data" -> cur12_las12_data,
				"misMatchHospital" -> mismatch_lst
			)), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	/**
		* @author liwei
		* @param query
		* @return
		*/
	def query_cel_data(query: DBObject): JsValue ={
		// TODO : 当期/上期/去年同期
		/**
			* cel
			* 	1. cur 当期
			*   2. ear 上期
			*   3. las 去年同期
			* hospNum 医院数量
			* hospNum 产品数量
			* marketNum 市场数量
			* sales 销售额
			* units 销售量
			* date 日期
			*/
		val data = _data_connection_cores.getCollection("FactResult").find(query)
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
			date = DateUtils.Timestamp2yyyyMM(obj.get("Date").asInstanceOf[Number].longValue())
		}
		toJson(Map(
			"HospNum" -> toJson(hospNum),
			"ProductNum" -> toJson(productNum),
			"MarketNum" -> toJson(marketNum),
			"Sales" -> toJson(sales),
			"Units" -> toJson(units),
			"Date" -> toJson(date)
		))
	}

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @param query_type
		* @return
		*/
	def query(company: String,market: String,date: String,query_type: String): DBObject ={
		/**
			* cur 当期
			* ear 上期
			* las 去年同期
			* cur12 当年近12月
			* las12 去年近12月
			*/
		query_type match {
			case "cur" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2Long(date)))
			case "ear" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2EarlyLong(date)))
			case "las" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2LastLong(date)))
			case "cur12" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$gte" -> DateUtils.MMyyyy2Early12Long(date).head,"$lt" -> DateUtils.MMyyyy2Early12Long(date).tail.head))
			case "las12" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$gte" -> DateUtils.MMyyyy2Last12Long(date).head,"$lt" -> DateUtils.MMyyyy2Last12Long(date).tail.head))
		}
	}

	/**
		* @author liwei
		* @param query
		* @param data_type
		* @param cur_data
		* @return
		*/
	def HPM_Near12(date: String,query: DBObject,data_type: String)(cur_data: JsValue): JsValue ={
		/**
			* H 医院
			* P 商品
			* M 市场
			*/
		val data = _data_connection_cores.getCollection("SampleCheckResult").find(query).sort(MongoDBObject("Date" -> 1))
		val date_sales_sb = new ListBuffer[JsValue]()
		while (data.hasNext) {
			val obj = data.next()
			val cur_date = DateUtils.Timestamp2yyyyMM(obj.get("Date").asInstanceOf[Number].longValue())
			data_type match {
				case "H" => date_sales_sb.append(toJson(Map("HospNum" -> toJson(obj.get("HospNum").asInstanceOf[Number].intValue()),"Date" -> toJson(cur_date))))
				case "P" => date_sales_sb.append(toJson(Map("ProductNum" -> toJson(obj.get("ProductNum").asInstanceOf[Number].intValue()),"Date" -> toJson(cur_date))))
				case "M" => date_sales_sb.append(toJson(Map("MarketNum" -> toJson(obj.get("MarketNum").asInstanceOf[Number].intValue()),"Date" -> toJson(cur_date))))
			}
		}
		if(!date_sales_sb.map(z => (z \ "Date").asOpt[String].get).exists(x => x.equals((cur_data \ "Date").asOpt[String].get))){
			var cur_date = (cur_data \ "Date").asOpt[String].get
			if(cur_date.equals("")){
				cur_date = DateUtils.MMyyyy2yyyyMM(date)
			}
			data_type match {
				case "H" => date_sales_sb.append(toJson(Map("HospNum" -> toJson((cur_data \ "HospNum").asOpt[Int].get),"Date" -> toJson(cur_date))))
				case "P" => date_sales_sb.append(toJson(Map("ProductNum" -> toJson((cur_data \ "ProductNum").asOpt[Int].get),"Date" -> toJson(cur_date))))
				case "M" => date_sales_sb.append(toJson(Map("MarketNum" -> toJson((cur_data \ "MarketNum").asOpt[Int].get),"Date" -> toJson(cur_date))))
			}
		}
		toJson(date_sales_sb)
	}

	/**
		* @author liwei
		* @param cur_query
		* @param las_query
		* @param cur_data
		* @return
		*/
	def SU_Near12(date: String,cur_query: DBObject,las_query: DBObject)(cur_data: JsValue): JsValue ={
		/**
			* date_sales_cur_sb	近12月得销售额、销售量、日期
			* date_sales_las_sb 去年同期近12月得销售额、销售量、日期
			* new_date_sales_cur_sb 近12月得销售额、去年同期近12月得销售额、日期
			* new_date_sales_las_sb 近12月得销售量、去年同期近12月得销售量、日期
			* x_date 近12月得日期
			* x_month 近12月得月份
			* y_date 去年同期近12月得日期
			* y_month 去年同期近12月得月份
			*/
		val cur12_data = _data_connection_cores.getCollection("SampleCheckResult").find(cur_query).sort(MongoDBObject("Date" -> 1))
		val date_sales_cur_sb = new ListBuffer[JsValue]()

		while (cur12_data.hasNext) {
			val obj = cur12_data.next()
			date_sales_cur_sb.append(toJson(Map("Units" -> toJson(obj.get("Units").asInstanceOf[Number].doubleValue()),"Sales" -> toJson(obj.get("Sales").asInstanceOf[Number].doubleValue()),"Date" -> toJson(DateUtils.Timestamp2yyyyMM(obj.get("Date").asInstanceOf[Number].longValue())))))
		}

		if(!date_sales_cur_sb.map(z => (z \ "Date").asOpt[String].get).exists(x => x.equals((cur_data \ "Date").asOpt[String].get))){
			date_sales_cur_sb.append(toJson(Map("Units" -> toJson((cur_data \ "Units").asOpt[Double].get),"Sales" -> toJson((cur_data \ "Sales").asOpt[Double].get),"Date" -> toJson((cur_data \ "Date").asOpt[String].get))))
		}

		val las12_data = _data_connection_cores.getCollection("SampleCheckResult").find(las_query).sort(MongoDBObject("Date" -> 1))
		val date_sales_las_sb = new ListBuffer[JsValue]()
		while (las12_data.hasNext) {
			val obj = cur12_data.next()
			date_sales_las_sb.append(toJson(Map("Units" -> toJson(obj.get("Units").asInstanceOf[Number].doubleValue()),"Sales" -> toJson(obj.get("Sales").asInstanceOf[Number].doubleValue()),"Date" -> toJson(DateUtils.Timestamp2yyyyMM(obj.get("Date").asInstanceOf[Number].longValue())))))
		}

		val new_date_sales_cur_sb = new ListBuffer[JsValue]()
		val new_date_sales_las_sb = new ListBuffer[JsValue]()
		date_sales_cur_sb foreach { x =>
			var x_date = (x \ "Date").asOpt[String].get
			if(x_date.equals("")){
				x_date = DateUtils.MMyyyy2yyyyMM(date)
			}
			val x_month = x_date.substring(4,x_date.length)

			date_sales_las_sb.size match {
				case 0 => {
					new_date_sales_cur_sb.append(toJson(Map("cur_Sales" -> toJson((x \ "Sales").asOpt[Double].get),"las_Sales" -> toJson(0.0),"Date" -> toJson(x_date))))
					new_date_sales_las_sb.append(toJson(Map("cur_Units" -> toJson((x \ "Units").asOpt[Double].get),"las_Units" -> toJson(0.0),"Date" -> toJson(x_date))))
				}
				case _ => {
					date_sales_las_sb foreach { y =>
						val y_date = (y \ "Date").asOpt[String].get
						val y_month = y_date.substring(4,y_date.length)
						if(x_month.equals(y_month)){
							new_date_sales_cur_sb.append(toJson(Map("cur_Sales" -> toJson((x \ "Sales").asOpt[Double].get),"las_Sales" -> toJson((y \ "Sales").asOpt[Double].get),"Date" -> toJson(x_date))))
							new_date_sales_las_sb.append(toJson(Map("cur_Units" -> toJson((x \ "Units").asOpt[Double].get),"las_Units" -> toJson((y \ "Units").asOpt[Double].get),"Date" -> toJson(x_date))))
						}
					}
				}
			}
		}
		toJson(Map("cur12_las12_Sales" -> toJson(new_date_sales_cur_sb),"cur12_las12_Units" -> toJson(new_date_sales_las_sb)))
	}

	/**
		* @author liwei
		* @param query
		* @return
		*/
	def misMatchHospital(query: DBObject): JsValue ={
		val data = _data_connection_cores.getCollection("FactResult").find(query)
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