package module.business

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar

import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.pattern.CommonMessage
import play.api.libs.json.JsObject
import com.pharbers.aqll.util.dao.from
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.util.dao._data_connection_cores
import com.pharbers.aqll.util.dao.Page._

import scala.math.Pi

object TempResultModuleMessage {
	sealed class msg_tempResultBase extends CommonMessage
	case class msg_tempResult(data : JsValue) extends msg_tempResultBase
}

object TempResultModule extends ModuleTrait {
	import TempResultModuleMessage._
	import controllers.common.default_error_handler.f
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_tempResult(data) => msg_finalresult_func(data)
		case _ => ???
	}
	
	def msg_finalresult_func(data : JsValue)(implicit error_handler : Int => JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {

		var market = (data \ "market").asOpt[List[String]].map (x => x).getOrElse(List())
		var staend = (data \ "staend").asOpt[List[String]].map (x => x).getOrElse(List())
		val fmomat_f = new SimpleDateFormat("MM/yyyy")
		val format_t = new SimpleDateFormat("yyyyMM")
		var conditions = MongoDBObject()

		market.size match {
			case 0 => conditions = MongoDBObject("Date" -> MongoDBObject("$gte" -> format_t.format(fmomat_f.parse(staend.head)).toInt,"$lt" -> format_t.format(fmomat_f.parse(staend.tail.head)).toInt))
			case _ => conditions = MongoDBObject("Market" -> MongoDBObject("$in" -> market),"Date" -> MongoDBObject("$gte" -> format_t.format(fmomat_f.parse(staend.head)).toInt,"$lt" -> format_t.format(fmomat_f.parse(staend.tail.head)).toInt))
		}
		println(conditions)
	  val currentPage = (data \ "currentPage").asOpt[Int].map (x => x).getOrElse(3)
    val order = "Date"
		val company = (data \ "company").asOpt[String].get

		try {
			val result = (from db() in company where conditions).selectSkipTop(SKIP(currentPage))(TAKE)(order)(finalResultTempJsValue(_))(_data_connection_cores).toList
			val total = (from db() in company where conditions).count(_data_connection_cores)
			println(s"result=${result} total=${total}")
			(Some(Map("finalResult" -> toJson(result), "page" -> toJson(Page(currentPage,total)))), None)
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def finalResultTempJsValue(x : MongoDBObject) : Map[String,JsValue] = {
		//val timeDate = Calendar.getInstance
		//timeDate.setTimeInMillis(x.getAs[Number]("Date").get.longValue())
		//var year = timeDate.get(Calendar.YEAR).toString
		//var month = (timeDate.get(Calendar.MONTH)+1).toString
		Map(
			//"Date" -> toJson(year + (if(month.length<2){"0"+month}else{month})),
			"Date" -> toJson(x.getAs[Number]("Date").get.longValue()),
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