package module

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.dao._data_connection_basic
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.util.{DateUtils, MD5}
import module.common.alMessage._

object MarketManageModuleMessage {
    sealed class msg_MarketManageBase extends CommonMessage
    case class msg_marketmanage_query(data: JsValue) extends msg_MarketManageBase
    case class msg_marketmanage_delete(data: JsValue) extends msg_MarketManageBase
    case class msg_marketmanage_findOne(data: JsValue) extends msg_MarketManageBase
    case class msg_marketmanage_save(data: JsValue) extends msg_MarketManageBase
}

object MarketManageModule extends ModuleTrait {
    import MarketManageModuleMessage._
    import controllers.common.default_error_handler.f
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_marketmanage_query(data) => query_func(data)
        case msg_marketmanage_delete(data) => delete_func(data)
        case msg_marketmanage_findOne(data) => findOne_func(data)
        case msg_marketmanage_save(data) => save_func(data)
    }

    /**
      * 检索市场列表
      *
      * @author liwei
      * @param data
      * @param error_handler
      * @return
      */
    def query_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> toJson(query))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    /**
      * 批量删除市场
      * 单次删除市场
      *
      * @author liwei
      * @param data
      * @param error_handler
      * @return
      */
    def delete_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val ids = (data \ "Market_Id").get.asOpt[List[String]].getOrElse(Nil)
            val r = ids map(x => _data_connection_basic.getCollection("Market").findAndRemove(MongoDBObject("Market_Id" -> x)))
            println(r)
            val result = r.size match {
                case i if i.equals(ids.size) => getMessage(1)
                case _ => getMessage(2)
            }
            (Some(Map("result" -> result)),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    /**
      * 根据ID查询
      *
      * @author liwei
      * @param data
      * @param error_handler
      * @return
      */
    def findOne_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val Market_Id = (data \ "Market_Id").get.asOpt[String].getOrElse("")
            val query =MongoDBObject("Market_Id" -> Market_Id)
            val dbo = _data_connection_basic.getCollection("Market").findOne(query)
            val result = dbo match {
                case None => getMessage(4)
                case _ => toJson(Map("result" -> toJson(Map("Market_Id" -> toJson(dbo.get.get("Market_Id").asInstanceOf[String]),"Market_Name" -> toJson(dbo.get.get("Market_Name").asInstanceOf[String]))),"status" -> toJson("success")))
            }
            (Some(Map("result" -> result)),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    /**
      * 保存市场信息
      *
      * @author liwei
      * @param data
      * @param error_handler
      * @return
      */
    def save_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val Market_Name = (data \ "Market_Name").get.asOpt[String].getOrElse("")
            val au = (data \ "au").get.asOpt[String].getOrElse("")
            au match {
                case "add" => {
                    val query = MongoDBObject("Market_Name" -> Market_Name)
                    val dbo = _data_connection_basic.getCollection("Market").findOne(query)
                    val result = dbo match {
                        case None => {
                            val market = MongoDBObject("Market_Id" -> MD5.md5(Market_Name),"Market_Name"-> Market_Name,"Date" -> System.currentTimeMillis())
                            val r = _data_connection_basic.getCollection("Market").insert(market)
                            r.getN match {
                                case 0 => getMessage(1)
                                case _ => getMessage(2)
                            }
                        }
                        case _ => getMessage(3)
                    }
                    (Some(Map("result" -> result)),None)
                }
                case "update" => {
                    val Market_Id = (data \ "Market_Id").get.asOpt[String].getOrElse("")
                    val query = MongoDBObject("Market_Id" -> Market_Id)
                    val update = MongoDBObject("Market_Id" -> MD5.md5(Market_Name),"Market_Name" -> Market_Name,"Date" -> System.currentTimeMillis())
                    val r = _data_connection_basic.getCollection("Market").update(query,update)
                    val result = r.getN match {
                        case 1 => getMessage(1)
                        case _ => getMessage(2)
                    }
                    (Some(Map("result" -> result)),None)
                }
            }
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    /**
      * 检索市场列表
      *
      * @author liwei
      * @return
      */
    def query : List[JsValue] = _data_connection_basic.getCollection("Market").find().map(x => toJson(Map("Market_Id" -> toJson(x.get("Market_Id").asInstanceOf[String]),"Market_Name" -> toJson(x.get("Market_Name").asInstanceOf[String]),"Date" -> toJson(DateUtils.Timestamp2yyyyMMdd(x.get("Date").asInstanceOf[Number].longValue()))))).toList
}