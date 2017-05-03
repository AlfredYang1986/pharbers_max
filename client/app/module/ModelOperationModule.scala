package module

import com.mongodb.casbah.Imports.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.dao._data_connection_cores
import com.pharbers.aqll.util.{DateUtils, GetProperties, HTTP}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.{alRestDate, alOperation}
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
			val uuid = queryUUID(company)
			uuid match {
				case None => (Some(Map("result" -> toJson("None"))), None)
				case _ => (Some(Map("result" -> alOperation.lst2Json(queryNear12(company,market,date,s"$company${uuid.get}"),1))), None)
			}
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def msg_operation_bar23_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val company = (data \ "company").asOpt[String].getOrElse("")
			val market = (data \ "market").asOpt[String].getOrElse("")
			val date = (data \ "date").asOpt[String].getOrElse("")
			val uuid = queryUUID(company)
			uuid match {
				case None => (Some(Map("result" -> toJson("None"))), None)
				case _ => {
					val temp_coll = s"$company${uuid.get}"
					val cur_top6 = queryCELData(company,market,date,temp_coll,"cur")(None,None)
					val ear_top6 = queryCELData(company,market,date,temp_coll,"ear")(cur_top6._2,cur_top6._3)
					val las_top6 = queryCELData(company,market,date,temp_coll,"las")(cur_top6._2,cur_top6._3)
					(Some(Map("result" -> toJson(
						Map("cur_top6" -> alOperation.lst2Json(cur_top6._1,2),"ear_top6" -> alOperation.lst2Json(ear_top6._1,2),"las_top6" -> alOperation.lst2Json(las_top6._1,2))
					))), None)
				}
			}
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @param temp_coll
		* @param ces
		* @param ols
		* @param olm
		* @return
		*/
	def queryCELData(company: String,market: String,date: String,temp_coll: String,ces: String)(ols: Option[List[String]],olm: Option[List[Map[String,Any]]]): (List[Map[String,Any]],Option[List[String]],Option[List[Map[String,Any]]]) = {
		val query = queryDBObject(market,date,ces,ols)
		val cur_size = _data_connection_cores.getCollection(temp_coll).count()
		val result = cur_size match {
			case 0 => _data_connection_cores.getCollection(company).find(query).sort(MongoDBObject("City" -> 1))
			case _ => _data_connection_cores.getCollection(temp_coll).find(query).sort(MongoDBObject("City" -> 1))
		}
		val list = new ListBuffer[Map[String,Any]]()
		var city = ""
		var sales_sum = 0.0
		while (result.hasNext) {
			val obj = result.next()
			val obj_city = obj.get("City").asInstanceOf[String]
			city match {
				case i if i.equals("") => city = obj_city
				case i if i.equals(obj_city) => sales_sum = sales_sum + obj.get("f_sales").asInstanceOf[Number].doubleValue()
				case i if !i.equals(obj_city) => {
					list.append(Map("City" -> city,"f_sales" -> sales_sum))
					sales_sum = 0.0
					city = obj_city
				}
			}
		}
		var A : List[Map[String,Any]] = Nil
		var B : Option[List[String]] = None
		var C : Option[List[Map[String,Any]]] = None
		olm match {
			case None => {
				A = list.toList.sortBy(x => x.get("f_sales").get.asInstanceOf[Number].doubleValue()).reverse.slice(0,6)
				B = Some(A.map(x => x.get("City").get.asInstanceOf[String]))
				C = Some(A.map(x => Map("City" -> x.get("City").get.asInstanceOf[String],"f_sales" -> 0.0)))
			}
			case _ => {
				C = olm
				B = ols
				A = alOperation.matchCityData(list.toList)(C.get)
			}
		}
		//println(s"$A \n $B \n $C")
		(A,B,C)
	}

	/**
		* @author liwei
		* @param market
		* @param date
		* @param el
		* @param list
		* @return
		*/
	def queryDBObject(market: String,date: String,el: String,list: Option[List[String]]): DBObject ={
		val ces_date = el match {
			case "cur" => DateUtils.MMyyyy2Long(date)
			case "ear" => DateUtils.MMyyyy2EarlyLong(date)
			case "las" => DateUtils.MMyyyy2LastLong(date)
		}
		list match {
			case None => MongoDBObject("Market" -> market,"Date" -> MongoDBObject("$eq" -> ces_date))
			case _ => MongoDBObject("Market" -> market,"City" -> MongoDBObject("$in" -> list),"Date" -> MongoDBObject("$eq" -> ces_date))
		}
	}

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @param temp_coll
		* @return
		*/
	def queryNear12(company: String,market: String,date: String,temp_coll: String): List[Map[String,Any]] ={
		val cur_date = DateUtils.MMyyyy2yyyyMM(date)
		val date_lst_str = alRestDate.diff12Month(cur_date)
		val query = MongoDBObject("Market" -> market,"Date" -> MongoDBObject("$in" -> DateUtils.ArrayDate2ArrayTimeStamp(alRestDate.diff12Month(cur_date))))
		_data_connection_cores.getCollection(temp_coll).count() match {
			case 0 => {
				val list_map = _data_connection_cores.getCollection(company).find(query).sort(MongoDBObject("Date" -> 1)).map(x =>
					Map("Date" -> DateUtils.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
						"f_sales" -> x.get("f_sales")
					)).toList
				alOperation.matchDateData(date_lst_str,list_map :: Nil)
			}
			case _ => {
				val temp_list_map = _data_connection_cores.getCollection(temp_coll).find(query).sort(MongoDBObject("Date" -> 1)).map(x =>
					Map("Date" -> DateUtils.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
						"f_sales" -> x.get("f_sales")
					)).toList
				val fina_list_map = _data_connection_cores.getCollection(company).find(query).sort(MongoDBObject("Date" -> 1)).map(x =>
					Map("Date" -> DateUtils.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
						"f_sales" -> x.get("f_sales")
					)).toList
				alOperation.matchDateData(date_lst_str,temp_list_map :: fina_list_map :: Nil)
			}
		}
	}

	def queryUUID(company: String): Option[String] = {
		val uuidjson = call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/queryUUID", toJson(Map("company" -> toJson(company))))
		Some((uuidjson \ "result").asOpt[String].get)
	}

	def call(uri: String, data: JsValue): JsValue = {
		val json = (HTTP(uri)).post(data).as[JsValue]
		json
	}
}