package module

import java.text.SimpleDateFormat
import java.util.Calendar
import com.pharbers.aqll.util.DateUtils
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.page.Page._
import com.pharbers.aqll.util.dao.{_data_connection_cores, from}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.util.StringUtils
object ResultQueryModuleMessage {
	sealed class msg_resultqueryBase extends CommonMessage
	case class msg_finalresult(data : JsValue) extends msg_resultqueryBase
}

object ResultQueryModule extends ModuleTrait {
	import ResultQueryModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_finalresult(data) => msg_finalresult_func(data)
		case _ => ???
	}

	def msg_finalresult_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {

		var market = (data \ "market").asOpt[List[String]].map (x => x).getOrElse(List())
		market = market.map(x => StringUtils.removeSpace(x))
		var staend = (data \ "staend").asOpt[List[String]].map (x => x).getOrElse(List())
		val fmomat_f = new SimpleDateFormat("MM/yyyy")
		var conditions = MongoDBObject()

		market.size match {
			case 0 => conditions = MongoDBObject("Date" -> MongoDBObject("$gte" -> fmomat_f.parse(staend.head).getTime,"$lt" -> fmomat_f.parse(staend.tail.head).getTime))
			case _ => conditions = MongoDBObject("Market" -> MongoDBObject("$in" -> market),"Date" -> MongoDBObject("$gte" -> fmomat_f.parse(staend.head).getTime,"$lt" -> fmomat_f.parse(staend.tail.head).getTime))
		}
		val currentPage = (data \ "currentPage").asOpt[Int].map (x => x).getOrElse(3)
		val order = "Date"
		val company = (data \ "company").asOpt[String].get

		try {
			val result = (from db() in company where conditions).selectSkipTop(SKIP(currentPage))(TAKE)(order)(finalResultTempJsValue(_))(_data_connection_cores).toList
			val total = (from db() in company where conditions).count(_data_connection_cores)
			(Some(Map("finalResult" -> toJson(result), "page" -> toJson(Page(currentPage,total)))), None)
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def finalResultTempJsValue(x : MongoDBObject) : Map[String,JsValue] = {
		Map(
			"Date" -> toJson(DateUtils.Timestamp2yyyyMM(x.getAs[Number]("Date").get.longValue())),
			"Provice" -> toJson(x.getAs[String]("Provice").get),
			"City" -> toJson(x.getAs[String]("City").get),
			"Panel_ID" -> toJson(x.getAs[String]("Panel_ID").get),
			"Market" -> toJson(x.getAs[String]("Market").get),
			"Product" -> toJson(x.getAs[String]("Product").get),
			"Sales" -> toJson(f"${x.getAs[Number]("f_sales").get.doubleValue}%1.2f"),
			"Units" -> toJson(f"${x.getAs[Number]("f_units").get.doubleValue}%1.2f")
		)
	}
}