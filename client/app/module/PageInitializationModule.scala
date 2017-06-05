package module

import com.pharbers.aqll.common.alDate.scala.alDateOpt.Timestamp2yyyyMM
import com.pharbers.aqll.common.alErrorCode.alErrorCode.errorToJson
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

/**
  * Created by liwei on 2017/6/5.
  */
object PageInitializationModuleMessage {
    sealed class msg_loadPageDataBase extends CommonMessage
    case class msg_loadPageData(data : JsValue) extends msg_loadPageDataBase
}

object PageInitializationModule extends ModuleTrait {
    import PageInitializationModuleMessage._
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_loadPageData(data) => msg_loadPageData_func(data)
        case _ => ???
    }

    def msg_loadPageData_func(data : JsValue)(implicit db: MongoDBModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val dttype = (data \ "dttype").get.asOpt[String].getOrElse("")

            val js = dttype match {
                case "fileupload" => queryDefault
                case "samplecheck" => queryOther
                case "samplereport" => queryDefault
                case "resultcheck" => queryOther
                case "resultquery" => queryDefault
            }
            (successToJson(js), None)
        } catch {
            case e: Exception => (None, Some(errorToJson(e.getMessage())))
        }
    }

    def queryDefault(implicit db: MongoDBModule) : JsValue = {
        val markets = db.basic.getCollection("Market").find().toList.map(x => toJson(x.get("Market_Name").asInstanceOf[String]))
        toJson(Map("markets" -> toJson(markets),"dates" -> toJson("")))
    }

    def queryOther(implicit db: MongoDBModule) : JsValue = {
        val markets = db.cores.getCollection("FactResult").find().toList.groupBy(x => x.get("Market")).toList.map(y => toJson(y._1.asInstanceOf[String]))
        val dates = db.cores.getCollection("FactResult").find().toList.groupBy(x => x.get("Date")).map(y => toJson(Map("code" -> toJson(y._1.asInstanceOf[Number].longValue()), "name" -> toJson(Timestamp2yyyyMM(y._1.asInstanceOf[Number].longValue()))))).toList
        toJson(Map("markets" -> toJson(markets),"dates" -> toJson(dates)))
    }
}