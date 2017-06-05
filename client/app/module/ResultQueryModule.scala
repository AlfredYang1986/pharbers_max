package module

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.common.Page._
import com.pharbers.aqll.common.alDao.from
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.common.alString.alStringOpt._
import com.pharbers.aqll.common.alDate.scala.alDateOpt._
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.dbmodule.MongoDBModule

object ResultQueryModuleMessage {
	sealed class msg_resultqueryBase extends CommonMessage
	case class msg_resultquery(data : JsValue) extends msg_resultqueryBase
}

object ResultQueryModule extends ModuleTrait {
	import ResultQueryModuleMessage._
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_resultquery(data) => resultquery_func(data)
		case _ => ???
	}

	def resultquery_func(data : JsValue)(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
		var markets = (data \ "market").asOpt[List[String]].map (x => x).getOrElse(Nil)
		var dates = (data \ "staend").asOpt[List[String]].map (x => x).getOrElse(Nil)

		markets = markets.map(x => removeSpace(x))

		val conditions = markets match {
			case Nil => {
				MongoDBObject("Date" -> MongoDBObject("$gte" -> MMyyyy2Long(dates.head),"$lt" -> MMyyyy2Long(dates.tail.head)))
			}
			case _ => {
				MongoDBObject("Market" -> MongoDBObject("$in" -> markets),"Date" -> MongoDBObject("$gte" -> MMyyyy2Long(dates.head),"$lt" -> MMyyyy2Long(dates.tail.head)))
			}
		}
		val currentPage = (data \ "currentPage").asOpt[Int].map (x => x).getOrElse(3)
		val company = (data \ "company").asOpt[String].get
		try {
			val result = (from db() in company where conditions).selectSkipTop(SKIP(currentPage))(TAKE)("Date")(calc_resultquery(_))(db.cores).toList
			lazy val total = (from db() in company where conditions).count(db.cores)
			(successToJson(toJson(result),toJson(Page(currentPage,total))), None)
		} catch {
			case ex : Exception => (None, Some(errorToJson(ex.getMessage())))
		}
	}

	def calc_resultquery(x : MongoDBObject) : Map[String,JsValue] = {
		Map(
			"Date" -> toJson(Timestamp2yyyyMM(x.getAs[Number]("Date").get.longValue())),
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