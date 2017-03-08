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
	    def dateListConditions(getter : JsValue => Any)(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
          case None => None
          case Some(x) => {
	          val fm = new SimpleDateFormat("MM/yyyy")
              val start = fm.parse(x.asInstanceOf[List[String]].head).getTime
              val end = fm.parse(x.asInstanceOf[List[String]].last).getTime
              Some("Date" $gte start $lte end)
          }
        }

		def marketListConditions(getter : JsValue => Any)(key : String, value : JsValue) : Option[DBObject] = getter(value) match {
			case None => None
			case Some(x) => {
				val lst = x.asInstanceOf[List[String]].map { str => str }
				Some("Market" $in lst)
			}
		}

	    def conditionsAcc(o : List[DBObject], keys : List[String], func : (String, JsValue) => Option[DBObject]) : List[DBObject] = keys match {
          case Nil => o
          case head :: lst => func(head, (data \ head).as[JsValue]) match {
              case None => conditionsAcc(o, lst, func)
              case Some(y) => conditionsAcc(y :: o, lst, func)
          }
        }
	    
	    def conditions : List[DBObject] = {
	        var con = conditionsAcc(Nil, "Date" :: Nil, dateListConditions(x => x.asOpt[List[String]]))
			con = conditionsAcc(con, "market" :: Nil, marketListConditions(x => x.asOpt[List[String]]))
	        con
	    }
	    
	    val currentPage = (data \ "currentPage").asOpt[Int].map (x => x).getOrElse(3)
        val order = "Date"
		val connectionName = (data \ "company").asOpt[String].get
		try {
            val result = (from db() in connectionName where $and(conditions)).selectSkipTop(SKIP(currentPage))(TAKE)(order)(finalResultTempJsValue(_))(_data_connection_cores).toList
            val total = (from db() in connectionName where $and(conditions)).count(_data_connection_cores)
			(Some(Map("finalResult" -> toJson(result), "page" -> toJson(Page(currentPage,total)))), None)
		} catch {
			case ex : Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def finalResultTempJsValue(x : MongoDBObject) : Map[String,JsValue] = {
		val timeDate = Calendar.getInstance
		timeDate.setTimeInMillis(x.getAs[Number]("Date").get.longValue())
		var year = timeDate.get(Calendar.YEAR).toString
		var month = (timeDate.get(Calendar.MONTH)+1).toString
		Map(
			"Date" -> toJson(year + (if(month.length<2){"0"+month}else{month})),
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