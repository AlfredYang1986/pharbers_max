package module

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.{DateUtils, GetProperties, HTTP}
import com.pharbers.aqll.util.dao.{_data_connection_cores, from}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.mutable.ListBuffer

object ModelOperationModuleMessage {
	sealed class msg_mondelOperationBase extends CommonMessage
	case class msg_operationBar11(data : JsValue) extends msg_mondelOperationBase
	case class msg_operationBar23(data : JsValue) extends msg_mondelOperationBase
}

object ModelOperationModule extends ModuleTrait {
	import ModelOperationModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_operationBar11(data) => msg_operation_bar11_func(data)
		case msg_operationBar23(data) => msg_operation_bar23_func(data)
		case _ => ???
	}
	
	def msg_operation_bar11_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val company = (data \ "company").asOpt[String].getOrElse("")
			val market = (data \ "market").asOpt[String].getOrElse("")
			val date = (data \ "date").asOpt[String].getOrElse("")
			val result = Echart1_Result(company,market,date,queryUUID(company))
			(Some(Map("result" -> result)), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def msg_operation_bar23_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val company = (data \ "company").asOpt[String].getOrElse("")
			val market = (data \ "market").asOpt[String].getOrElse("")
			val date = (data \ "date").asOpt[String].getOrElse("")
			val result = Echart2_Result(company,market,date,queryUUID(company))
			(Some(Map("result" -> result)), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def Echart1_Result(company: String,market: String,date: String,uuid: String): JsValue ={
		val cur_month_query = MongoDBObject("Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2Long(date)))

		val cur_month_data = _data_connection_cores.getCollection(company+uuid).find(cur_month_query)
		var cur_sales_sum = 0.0
		var cur_i = 0
		var cur_date = ""
		while (cur_month_data.hasNext) {
			val obj = cur_month_data.next()
			cur_sales_sum = cur_sales_sum+obj.get("f_sales").asInstanceOf[Number].doubleValue()
			if(cur_i<1){
				cur_date = DateUtils.Timestamp2yyyyMM(obj.get("Date").asInstanceOf[Number].longValue())
			}
			cur_i = cur_i+1
		}

		val lastyear_month_query_date = DateUtils.MMyyyy2Early12Long(date)
		val lastyear_month_query = MongoDBObject("Market" -> market,"Date" -> MongoDBObject("$gte" -> lastyear_month_query_date.head,"$lt" -> lastyear_month_query_date.tail.head))
		val lastyear_month_data = _data_connection_cores.getCollection(company).find(lastyear_month_query).sort(MongoDBObject("Date" -> 1))
		var lastyear_month_sum = 0.0
		var lastyear_month_i = 0
		var lastyear_month_date  = ""
		val sb12 = new ListBuffer[JsValue]()
		while (lastyear_month_data.hasNext) {
			val obj = lastyear_month_data.next()
			val date = DateUtils.Timestamp2yyyyMM(obj.get("Date").asInstanceOf[Number].longValue())
			if(lastyear_month_date.equals("")){
				lastyear_month_date = date
			}
			val sales_sum = obj.get("f_sales").asInstanceOf[Number].doubleValue()
			if(lastyear_month_date.equals(DateUtils.Timestamp2yyyyMM(obj.get("Date").asInstanceOf[Number].longValue()))){
				lastyear_month_sum = lastyear_month_sum+sales_sum
			}else{
				sb12.append(toJson(Map("f_sales" -> toJson(lastyear_month_sum),"Date" -> toJson(lastyear_month_date))))
				lastyear_month_sum = 0.0
				lastyear_month_sum = lastyear_month_sum+sales_sum
				lastyear_month_date  = date
			}
			lastyear_month_i = lastyear_month_i+1
		}
		sb12.append(toJson(Map("f_sales" -> toJson(cur_sales_sum),"Date" -> toJson(cur_date))))
		toJson(sb12)
	}

	def Echart2_Result(company: String,market: String,date: String,uuid: String): JsValue ={
		//Echart图2、3 当期数据组装
		val cur_month_query = MongoDBObject("Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2Long(date)))
		val cur_month_data = _data_connection_cores.getCollection(company+uuid).find(cur_month_query).sort(MongoDBObject("City" -> 1))
		var cur_sales_sum = 0.0
		var cur_i = 0
		var cur_city = ""
		val cur_sb = new ListBuffer[Map[String,Any]]()
		while (cur_month_data.hasNext) {
			val obj = cur_month_data.next()
			val cur_sales = obj.get("f_sales").asInstanceOf[Number].doubleValue()
			val cur_city_sub = obj.get("City").asInstanceOf[String]
			if(cur_city.equals("")){
				cur_city = cur_city_sub
			}
			if(cur_city.equals(cur_city_sub)){
				cur_sales_sum = cur_sales_sum + cur_sales
			}else{
				cur_sb.append(Map("f_sales" -> cur_sales_sum,"City" -> cur_city))
				cur_sales_sum = 0.0
				cur_sales_sum = cur_sales_sum + cur_sales
				cur_city = cur_city_sub
			}
			cur_i = cur_i+1
		}

		var cur_num = 0
		val cur_new_sb = new ListBuffer[JsValue]()
		val cur_new_city_sb = new ListBuffer[JsValue]()
		val cur_city_sb = new ListBuffer[String]()
		cur_sb.sortBy(x => x.get("f_sales").get.asInstanceOf[Number].doubleValue()).reverse.foreach{y =>
			if(cur_num<8){
				val f_sales = y.get("f_sales").get.asInstanceOf[Number].doubleValue()
				val City = y.get("City").get.asInstanceOf[String]
				cur_new_sb.append(toJson(Map("f_sales" -> toJson(f_sales),"City" -> toJson(City))))
				cur_new_city_sb.append(toJson(Map("f_sales" -> toJson(0.0),"City" -> toJson(City))))
				cur_city_sb.append(City)
			}
			cur_num = cur_num +1
		}

		//Echart图2 上期数据组装
		val ear_month_query = MongoDBObject("City" -> MongoDBObject("$in" -> cur_city_sb.toList),"Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2EarlyLong(date)))
		val ear_month_data = _data_connection_cores.getCollection(company+uuid).find(ear_month_query).sort(MongoDBObject("City" -> 1))
		var ear_sales_sum = 0.0
		var ear_i = 0
		var ear_city = ""
		val ear_sb = new ListBuffer[Map[String,Any]]()

		while (ear_month_data.hasNext) {
			val obj = ear_month_data.next()
			val ear_sales = obj.get("f_sales").asInstanceOf[Number].doubleValue()
			val ear_city_sub = obj.get("City").asInstanceOf[String]
			if(ear_city.equals("")){
				ear_city = ear_city_sub
			}
			if(ear_city.equals(ear_city_sub)){
				ear_sales_sum = ear_sales_sum + ear_sales
			}else{
				ear_sb.append(Map("f_sales" -> ear_sales_sum,"City" -> ear_city))
				ear_sales_sum = 0.0
				ear_sales_sum = ear_sales_sum + ear_sales
				ear_city = ear_city_sub
			}
			ear_i = ear_i+1
		}

		val ear_new_sb = new ListBuffer[JsValue]()
		cur_new_city_sb.foreach{x =>
			var jsv : JsValue = x
			if(ear_sb.size>0){
				ear_sb.foreach{y =>
					if((x \ "City").asOpt[String].get.equals(y.get("City").get.asInstanceOf[String])){
						jsv = toJson(Map("f_sales" -> toJson(y.get("f_sales").get.asInstanceOf[Number].doubleValue()),"City" -> toJson(y.get("City").get.asInstanceOf[String])))
					}
				}
			}else{
				jsv = toJson(Map("City" -> toJson((x \ "City").asOpt[String].get),"f_sales" -> toJson(0.0)))
			}
			ear_new_sb.append(jsv)
		}

		//Echart图3 去年同期数据组装
		val lastyear_month_query = MongoDBObject("City" -> MongoDBObject("$in" -> cur_city_sb.toList),"Market" -> market,"Date" -> MongoDBObject("$eq" -> DateUtils.MMyyyy2LastLong(date)))
		val lastyear_month_data = _data_connection_cores.getCollection(company+uuid).find(lastyear_month_query).sort(MongoDBObject("City" -> 1))
		var lastyear_sales_sum = 0.0
		var lastyear_i = 0
		var lastyear_city = ""
		val lastyear_sb = new ListBuffer[Map[String,Any]]()

		while (lastyear_month_data.hasNext) {
			val obj = lastyear_month_data.next()
			val lastyear_sales = obj.get("f_sales").asInstanceOf[Number].doubleValue()
			val lastyear_city_sub = obj.get("City").asInstanceOf[String]
			if(lastyear_city.equals("")){
				lastyear_city = lastyear_city_sub
			}
			if(lastyear_city.equals(lastyear_city_sub)){
				lastyear_sales_sum = lastyear_sales_sum + lastyear_sales
			}else{
				lastyear_sb.append(Map("f_sales" -> lastyear_sales_sum,"City" -> lastyear_city))
				lastyear_sales_sum = 0.0
				lastyear_sales_sum = lastyear_sales_sum + lastyear_sales
				lastyear_city = lastyear_city_sub
			}
			lastyear_i = lastyear_i+1
		}

		val lastyear_new_sb = new ListBuffer[JsValue]()
		cur_new_city_sb.foreach{x =>
			var jsv : JsValue = x
			if(lastyear_sb.size>0){
				lastyear_sb.foreach{y =>
					if((x \ "City").asOpt[String].get.equals(y.get("City").get.asInstanceOf[String])){
						jsv = toJson(Map("f_sales" -> toJson(y.get("f_sales").get.asInstanceOf[Number].doubleValue()),"City" -> toJson(y.get("City").get.asInstanceOf[String])))
					}
				}
			}else{
				jsv = toJson(Map("City" -> toJson((x \ "City").asOpt[String].get),"f_sales" -> toJson(0.0)))
			}
			lastyear_new_sb.append(jsv)
		}
		toJson(Map("cur_month_result" -> toJson(cur_new_sb.toList),"ear_month_result" -> toJson(ear_new_sb.toList),"lastyear_month_result" -> toJson(lastyear_new_sb.toList)) )
	}

	def queryUUID(company: String): String = {
		//val uuid = "fb9cb2cd-52ab-4493-b943-24800d85a610"
		val uuidjson = call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/queryUUID", toJson(Map("company" -> toJson(company))))
		println(uuidjson)
		(uuidjson \ "result").asOpt[String].get
	}

	def call(uri: String, data: JsValue): JsValue = {
		val json = (HTTP(uri)).post(data).as[JsValue]
		json
	}
}