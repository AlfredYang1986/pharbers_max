package module

import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.dao._data_connection_basic
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.util.{DateUtils, MD5}
import com.mongodb.casbah.MongoCollectionBase

object MarketManageModuleMessage {
    sealed class msg_MarketManageBase extends CommonMessage
    case class msg_marketmanage_query(data: JsValue) extends msg_MarketManageBase
    case class msg_marketmanage_delete(data: JsValue) extends msg_MarketManageBase
    case class msg_marketmanage_querybyid(data: JsValue) extends msg_MarketManageBase
    case class msg_marketmanage_save(data: JsValue) extends msg_MarketManageBase
}

object MarketManageModule extends ModuleTrait {
    import MarketManageModuleMessage._
    import controllers.common.default_error_handler.f
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_marketmanage_query(data) => query_func(data)
        case msg_marketmanage_delete(data) => delete_func(data)
        case msg_marketmanage_querybyid(data) => querybyid_func(data)
        case msg_marketmanage_save(data) => save_func(data)
    }

    def query_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> toJson(query))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def delete_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val Market_Id = (data \ "Market_Id").get.asOpt[String].getOrElse("")
            val dbo = _data_connection_basic.getCollection("Market").findAndRemove(MongoDBObject("Market_Id" -> Market_Id))
            val lst = dbo match {
                case None => Nil
                case _ => query
            }
            (Some(Map("result" -> toJson(lst))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def querybyid_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val Market_Id = (data \ "Market_Id").get.asOpt[String].getOrElse("")
            val query =MongoDBObject("Market_Id" -> Market_Id)
            val dbo = _data_connection_basic.getCollection("Market").findOne(query).get
            val result = Map("Market_Id" -> toJson(dbo.get("Market_Id").asInstanceOf[String]),"Market_Name" -> toJson(dbo.get("Market_Name").asInstanceOf[String]))
            (Some(Map("result" -> toJson(result))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def save_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val Market_Id = (data \ "Market_Id").get.asOpt[String].getOrElse("")
            val Market_Name = (data \ "Market_Name").get.asOpt[String].getOrElse("")
            val au = (data \ "au").get.asOpt[String].getOrElse("")
            println(s"au=$au id=$Market_Id name=$Market_Name")
            au match {
                case "a" => {
                    val market = MongoDBObject("Market_Id" -> MD5.md5(Market_Name),"Market_Name"-> Market_Name,"Date" -> System.currentTimeMillis())
                    _data_connection_basic.getCollection("Market").insert(market)
                }
                case "u" => {
                    val query = MongoDBObject("Market_Id" -> Market_Id)
                    val update = MongoDBObject("Market_Name" -> Market_Name)
                    _data_connection_basic.getCollection("Market").update(query,update)
                }
            }
            (Some(Map("result" -> toJson("ok"))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def query : List[JsValue] = _data_connection_basic.getCollection("Market").find().map(x => toJson(Map("Market_Id" -> toJson(x.get("Market_Id").asInstanceOf[String]),"Market_Name" -> toJson(x.get("Market_Name").asInstanceOf[String]),"Date" -> toJson(DateUtils.Timestamp2yyyyMM(x.get("Date").asInstanceOf[Number].longValue()))))).toList
}