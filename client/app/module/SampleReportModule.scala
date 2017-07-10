package module

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.common.alDao.from
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import scala.collection.mutable.ListBuffer
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.dbmodule.MongoDBModule

object SampleReportModuleMessage {
	sealed class msg_ReportBaseQuery extends CommonMessage
	case class msg_samplereport(data: JsValue) extends msg_ReportBaseQuery
}

object SampleReportModule extends ModuleTrait {
	import SampleReportModuleMessage._
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_samplereport(data) => msg_check_func(data)
	}

	def msg_check_func(data: JsValue)(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val company = (data \ "company").asOpt[String].getOrElse("")
		val query = MongoDBObject("Company" -> company)
		try {
			val market_lst = (from db() in "FactResult" where query).selectSort("Date")(MongoDBReport(_))(db.cores).toList
			val market_arr = market_lst.asInstanceOf[List[Map[String,Any]]].groupBy(x => x.get("Market").get)
			val lb = new ListBuffer[JsValue]()
			market_arr.foreach { x =>
				val date_lst_sb,dhp_lst_sb = new ListBuffer[JsValue]()
				x._2.foreach { y =>
					val date = y.get("Date").get
					date_lst_sb.append(toJson(s"$date"))
					val e_query = MongoDBObject("Company" -> company,"Market" -> y.get("Market").get,"Date" -> MongoDBObject("$eq" -> alDateOpt.yyyyMM2EarlyLong(date.toString)))
					val l_query = MongoDBObject("Company" -> company,"Market" -> y.get("Market").get,"Date" -> MongoDBObject("$eq" -> alDateOpt.yyyyMM2LastLong(y.get("Date").get.toString)))
					dhp_lst_sb.append(
						toJson(Map("Date" -> toJson(y.get("Date").get.toString),
							"c_HospNum" -> toJson(y.get("HospNum").get.toString),
							"c_ProductNum" -> toJson(y.get("ProductNum").get.toString),
							"e_HospNum" -> toJson(getNum((from db() in "SampleCheckResult" where e_query).select(queryProductNum(_))(db.cores).toList,"ProductNum").toString),
							"e_ProductNum" -> toJson(getNum((from db() in "SampleCheckResult" where e_query).select(queryHospNum(_))(db.cores).toList,"HospNum").toString),
							"l_HospNum" -> toJson(getNum((from db() in "SampleCheckResult" where l_query).select(queryProductNum(_))(db.cores).toList,"ProductNum").toString),
							"l_ProductNum" -> toJson(getNum((from db() in "SampleCheckResult" where l_query).select(queryHospNum(_))(db.cores).toList,"HospNum").toString)
						)))
				}
				lb.append(toJson(Map("Market" -> toJson(x._1.toString),"date_lst_sb" -> toJson(date_lst_sb.toList),"dhp_lst_sb" -> toJson(dhp_lst_sb.toList))))
			}
			(successToJson(toJson(lb)), None)
		} catch {
			case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
		}
	}

	def getNum(lst : List[Map[String,Any]],keystr : String) : Long = {
		lst match {
			case Nil => 0
			case i if i.equals(null) => 0
			case i if !i.equals(null) => lst.head.get(keystr).get.asInstanceOf[Number].longValue()
		}
	}

	def queryProductNum(d: MongoDBObject): Map[String,Any] = Map("ProductNum" -> d.getAs[Number]("ProductNum").get.longValue())

	def queryHospNum(d: MongoDBObject): Map[String,Any] = Map("HospNum" -> d.getAs[Number]("HospNum").get.longValue())

	def MongoDBReport(d: MongoDBObject): Map[String,Any] = {
		Map(
			"HospNum" -> d.getAs[Number]("HospNum").get.longValue(),
			"ProductNum" -> d.getAs[Number]("ProductNum").get.longValue(),
			"Market" -> d.getAs[String]("Market").get,
			"Date" -> alDateOpt.Timestamp2yyyyMM(d.getAs[Number]("Date").get.longValue())
		)
	}
}