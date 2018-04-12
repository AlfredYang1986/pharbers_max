package module

import play.api.libs.json._
import play.api.libs.json.Json.toJson
import scala.collection.mutable.ListBuffer
import com.mongodb.casbah.commons.MongoDBObject

import com.pharbers.ErrorCode._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.common.datatype.date.PhDateOpt
import com.pharbers.mongodbConnect.{connection_instance, from}
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

object SampleReportModuleMessage {
	sealed class msg_ReportBaseQuery extends CommonMessage("samplereport", SampleReportModule)
	case class msg_samplereport(data: JsValue) extends msg_ReportBaseQuery
}

object SampleReportModule extends ModuleTrait {
	import SampleReportModuleMessage._
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_samplereport(data) => msg_check_func(data)
	}

	def msg_check_func(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val company = (data \ "company").asOpt[String].getOrElse("")
		val query = MongoDBObject("Company" -> company)
		implicit val db = cm.modules.get.get("db").map (x => x.asInstanceOf[connection_instance]).getOrElse(throw new Exception("no db connection"))
		try {
			val market_lst = (from db() in "FactResult" where query).selectSort("Date")(MongoDBReport).toList
			val market_arr = market_lst.asInstanceOf[List[Map[String,Any]]].groupBy(x => x("Market"))
			val lb = new ListBuffer[JsValue]()
			market_arr.foreach { x =>
				val date_lst_sb,dhp_lst_sb = new ListBuffer[JsValue]()
				x._2.foreach { y =>
					val date = y("Date")
					date_lst_sb.append(toJson(s"$date"))
					val e_query = MongoDBObject("Company" -> company,"Market" -> y("Market"),"Date" -> MongoDBObject("$eq" -> PhDateOpt.yyyyMM2EarlyLong(date.toString)))
					val l_query = MongoDBObject("Company" -> company,"Market" -> y("Market"),"Date" -> MongoDBObject("$eq" -> PhDateOpt.yyyyMM2LastLong(y("Date").toString)))
					dhp_lst_sb.append(
						toJson(Map("Date" -> toJson(y("Date").toString),
							"c_HospNum" -> toJson(y("HospNum").toString),
							"c_ProductNum" -> toJson(y("ProductNum").toString),
							"e_HospNum" -> toJson(getNum((from db() in "SampleCheckResult" where e_query).select(queryProductNum).toList,"ProductNum").toString),
							"e_ProductNum" -> toJson(getNum((from db() in "SampleCheckResult" where e_query).select(queryHospNum).toList,"HospNum").toString),
							"l_HospNum" -> toJson(getNum((from db() in "SampleCheckResult" where l_query).select(queryProductNum).toList,"ProductNum").toString),
							"l_ProductNum" -> toJson(getNum((from db() in "SampleCheckResult" where l_query).select(queryHospNum).toList,"HospNum").toString)
						)))
				}
				lb.append(toJson(Map("Market" -> toJson(x._1.toString),"date_lst_sb" -> toJson(date_lst_sb.toList),"dhp_lst_sb" -> toJson(dhp_lst_sb.toList))))
			}
			(successToJson(toJson(lb)), None)
		} catch {
			case ex: Exception => (None, Some(errorToJson(ex.getMessage)))
		}
	}

	def getNum(lst : List[Map[String,Any]],keystr : String) : Long = {
		lst match {
			case Nil => 0
			case i if i.equals(null) => 0
			case i if !i.equals(null) => lst.head(keystr).asInstanceOf[Number].longValue()
		}
	}

	def queryProductNum(d: MongoDBObject): Map[String,Any] = Map("ProductNum" -> d.getAs[Number]("ProductNum").get.longValue())

	def queryHospNum(d: MongoDBObject): Map[String,Any] = Map("HospNum" -> d.getAs[Number]("HospNum").get.longValue())

	def MongoDBReport(d: MongoDBObject): Map[String,Any] = {
		Map(
			"HospNum" -> d.getAs[Number]("HospNum").get.longValue(),
			"ProductNum" -> d.getAs[Number]("ProductNum").get.longValue(),
			"Market" -> d.getAs[String]("Market").get,
			"Date" -> PhDateOpt.Timestamp2yyyyMM(d.getAs[Number]("Date").get.longValue())
		)
	}
}