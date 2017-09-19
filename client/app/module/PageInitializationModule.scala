package module

import com.pharbers.aqll.common.DBConection
import com.pharbers.aqll.common.alDate.scala.alDateOpt.Timestamp2yyyyMM
import com.pharbers.aqll.common.alErrorCode.alErrorCode.errorToJson
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait

/**
  * Created by liwei on 2017/6/5.
  */
object PageInitializationModuleMessage {
    sealed class msg_loadPageDataBase extends CommonMessage("initialization", PageInitializationModule)
    case class msg_loadPageData(data : JsValue) extends msg_loadPageDataBase
}

object PageInitializationModule extends ModuleTrait {
    import PageInitializationModuleMessage._
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_loadPageData(data) => msg_loadPageData_func(data)
        case _ => ???
    }

    def msg_loadPageData_func(data : JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
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

    def queryDefault(implicit cm: CommonModules) : JsValue = {
//        val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        implicit val db = DBConection.basic
        val markets = db.getCollection("Market").find().toList.map(x => toJson(x.get("Market_Name").asInstanceOf[String]))
        toJson(Map("markets" -> toJson(markets),"dates" -> toJson("")))
    }

    def queryOther(implicit cm: CommonModules) : JsValue = {
//        val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        implicit val db = DBConection.cores
        val markets = db.getCollection("FactResult").find().toList.groupBy(x => x.get("Market")).toList.map(y => toJson(y._1.asInstanceOf[String]))
        val dates = db.getCollection("FactResult").find().toList.groupBy(x => x.get("Date")).map(y => toJson(Map("code" -> toJson(y._1.asInstanceOf[Number].longValue()), "name" -> toJson(Timestamp2yyyyMM(y._1.asInstanceOf[Number].longValue()))))).toList
        toJson(Map("markets" -> toJson(markets),"dates" -> toJson(dates)))
    }
}