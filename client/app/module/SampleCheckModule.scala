package module

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import module.common.alNearDecemberMonth
import com.pharbers.aqll.common.alDao.data_connection

import scala.collection.mutable.ListBuffer
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.dbmodule.MongoDBModule

object SampleCheckModuleMessage {
	sealed class msg_CheckBaseQuery extends CommonMessage
	case class msg_samplecheck(data: JsValue) extends msg_CheckBaseQuery
}

object SampleCheckModule extends ModuleTrait {
	import SampleCheckModuleMessage._

	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_samplecheck(data) => msg_check_func(data)
	}

	/**
		* @author liwei
		* @param data
		* @return
		*/
	def msg_check_func(data: JsValue)(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val company = (data \ "company").asOpt[String].getOrElse("")
		val market = (data \ "market").asOpt[String].getOrElse("")
		val date = (data \ "date").asOpt[String].getOrElse("")
		try {
			val cur12_date = matchThisYearData(alNearDecemberMonth.diff12Month(date),queryNearTwelveMonth(db.cores,company,market,date))
			val las12_date = matchLastYearData(alNearDecemberMonth.diff12Month(date),queryLastYearTwelveMonth(db.cores,company,market,date))

			val cur_data = query_cel_data(db.cores,query(company,market,date,"cur"))
			val ear_data = query_cel_data(db.cores,query(company,market,date,"ear"))
			val las_data = query_cel_data(db.cores,query(company,market,date,"las"))

			val mismatch_lst = misMatchHospital(db.cores,query(company,market,date,"cur"));

			(successToJson(toJson(Map(
				"cur_data" -> cur_data,
				"ear_data" -> ear_data,
				"las_data" -> las_data,
				"cur12_date" -> lsttoJson(cur12_date),
				"las12_date" -> lsttoJson(las12_date),
				"misMatchHospital" -> mismatch_lst
			))), None)
		} catch {
			case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
		}
	}

	def richDateArr(arr: Array[String]): List[Map[String,Any]] = {
		arr.map(x => Map("Date" -> x,"HospNum" -> 0,"ProductNum" -> 0,"MarketNum" -> 0,"Sales" -> 0.0,"Units" -> 0.0)).toList
	}

	def matchThisYearData(arr: Array[String],lst: List[List[Map[String,AnyRef]]]): List[Map[String,Any]] = {
		val date_lst = richDateArr(arr)

		val temp_head_lst = date_lst map { x =>
			val obj = lst.head.find(y => y.get("Date").get.equals(x.get("Date").get))
			obj match {
				case None => x
				case _ => obj.get
			}
		}

		val temp_tail_lst = date_lst map { x =>
			val obj = lst.tail.head.find(y => y.get("Date").get.equals(x.get("Date").get))
			obj match {
				case None => x
				case _ => obj.get
			}
		}

		temp_tail_lst map { x =>
			val obj = temp_head_lst.find(y => y.get("Date").get.equals(x.get("Date").get))
			obj match {
				case None => x
				case _ => obj.get
			}
		}
	}

	def matchLastYearData(arr: Array[String],lst: List[Map[String,AnyRef]]): List[Map[String,Any]] = {
		val date_lst = richDateArr(arr)
		lst match {
			case Nil => date_lst
			case _ => {
				date_lst map{ x =>
					val obj = lst.find(y => y.get("Date").get.equals(x.get("Date").get))
					obj match {
						case None => x
						case _ => obj.get
					}
				}
			}
		}
	}

	def lsttoJson(lst: List[Map[String,Any]]): JsValue ={
		toJson(lst.map{ x => toJson(
			Map(
				"Date" -> toJson(x.get("Date").get.asInstanceOf[String]),
				"HospNum" -> toJson(x.get("HospNum").get.asInstanceOf[Number].intValue()),
				"ProductNum" -> toJson(x.get("ProductNum").get.asInstanceOf[Number].intValue()),
				"MarketNum" -> toJson(x.get("MarketNum").get.asInstanceOf[Number].intValue()),
				"Sales" -> toJson(x.get("Sales").get.asInstanceOf[Number].doubleValue()),
				"Units" -> toJson(x.get("Units").get.asInstanceOf[Number].doubleValue())
			)
		)})
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
		case "cur" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> alDateOpt.yyyyMM2Long(date)))
		case "ear" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> alDateOpt.yyyyMM2EarlyLong(date)))
		case "las" => MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$eq" -> alDateOpt.yyyyMM2LastLong(date)))
	}

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @return
		*/
	def queryNearTwelveMonth(database: data_connection,company: String,market: String,date: String): List[List[Map[String,AnyRef]]] = {
		val date_lst = alDateOpt.ArrayDate2ArrayTimeStamp(alNearDecemberMonth.diff12Month(date))
		val query = MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$in" -> date_lst))
		val f_lst = database.getCollection("FactResult").find(query).sort(MongoDBObject("Date" -> 1))
		val s_lst = database.getCollection("SampleCheckResult").find(query).sort(MongoDBObject("Date" -> 1))
		List(f_lst.map(x => resulttomap(x)).toList,s_lst.map(x => resulttomap(x)).toList)
	}

	def resulttomap(x: DBObject) : Map[String,AnyRef] = Map(
		"Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
		"HospNum" -> x.get("HospNum"),
		"ProductNum" -> x.get("ProductNum"),
		"MarketNum" -> x.get("MarketNum"),
		"Sales" -> x.get("Sales"),
		"Units" -> x.get("Units"))

	/**
		* @author liwei
		* @param company
		* @param market
		* @param date
		* @return
		*/
	def queryLastYearTwelveMonth(database: data_connection,company: String,market: String,date: String): List[Map[String,AnyRef]] = {
		val date_lst = alDateOpt.ArrayDate2ArrayTimeStamp(alNearDecemberMonth.diff12Month(date))
		val query = MongoDBObject("Company" -> company,"Market" -> market,"Date" -> MongoDBObject("$in" -> date_lst))
		val lst = database.getCollection("SampleCheckResult").find(query).sort(MongoDBObject("Date" -> 1))
		lst.map(x => Map(
			"Date" -> alDateOpt.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()),
			"HospNum" -> x.get("HospNum"),"ProductNum" -> x.get("ProductNum"),
			"MarketNum" -> x.get("MarketNum"),
			"Sales" -> x.get("Sales"),
			"Units" -> x.get("Units"))).toList
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
				Mismatch.append(toJson(Map("Hosp_name" -> toJson(obj.get("Hosp_name").asInstanceOf[String]),"Province" -> toJson(obj.get("Province").asInstanceOf[String]),"City" -> toJson(obj.get("City").asInstanceOf[String]),"City_level" -> toJson(obj.get("City_level").asInstanceOf[String]))))
			}
		}
		toJson(Mismatch)
	}
}