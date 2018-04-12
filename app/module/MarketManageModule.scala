package module

import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import com.pharbers.ErrorCode._
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.common.datatype.date.PhDateOpt
import com.pharbers.mongodbConnect.connection_instance
import com.pharbers.sercuity.Sercurity.{md5Hash => md5}
import com.pharbers.bmmessages.{CommonMessage, CommonModules, MessageDefines}

object MarketManageModuleMessage {
    sealed class msg_MarketManageBase extends CommonMessage("marketmanage", MarketManageModule)
    case class msg_marketmanage_query(data: JsValue) extends msg_MarketManageBase
    case class msg_marketmanage_delete(data: JsValue) extends msg_MarketManageBase
    case class msg_marketmanage_findOne(data: JsValue) extends msg_MarketManageBase
    case class msg_marketmanage_save(data: JsValue) extends msg_MarketManageBase
}

object MarketManageModule extends ModuleTrait {
    import MarketManageModuleMessage._
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_marketmanage_query(data) => queryMarkets_func(data)
        case msg_marketmanage_delete(data) => deleteMarkets_func(data)
        case msg_marketmanage_findOne(data) => findOneMarket_func(data)
        case msg_marketmanage_save(data) => saveMarket_func(data)
    }

    /**
      * 检索市场列表
      *
      * @author liwei
      * @param data
      * @return
      */
    def queryMarkets_func(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val db = cm.modules.get.get("db").map (x => x.asInstanceOf[connection_instance]).getOrElse(throw new Exception("no db connection"))
        try {
            val result = db.getCollection("Market").find().map(x =>
                toJson(Map(
                    "Market_Id" -> toJson(x.get("Market_Id").asInstanceOf[String]),
                    "Market_Name" -> toJson(x.get("Market_Name").asInstanceOf[String]),
                    "Date" -> toJson(PhDateOpt.Timestamp2yyyyMMdd(x.get("Date").asInstanceOf[Number].longValue()))
                ))).toList
            (successToJson(toJson(result)), None)
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
        }
    }

    /**
      * 批量删除市场
      * 单次删除市场
      *
      * @author liwei
      * @param data
      * @return
      */
    def deleteMarkets_func(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val db = cm.modules.get.get("db").map (x => x.asInstanceOf[connection_instance]).getOrElse(throw new Exception("no db connection"))
        try {
            val market_ids = (data \ "Market_Id").get.asOpt[List[String]].getOrElse(throw new Exception("info select markets you want to delete"))
            val result = market_ids map (x => db.getCollection("Market").findAndRemove(MongoDBObject("Market_Id" -> x)))
            result.size match {
                case i if i.equals(market_ids.size) => (successToJson(toJson(getErrorMessageByName("warn operation success"))), None)
                case _ => throw new Exception("warn operation failed")
            }
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
        }
    }

    /**
      * 根据ID查询
      *
      * @author liwei
      * @param data
      * @return
      */
    def findOneMarket_func(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val db = cm.modules.get.get("db").map (x => x.asInstanceOf[connection_instance]).getOrElse(throw new Exception("no db connection"))
        try {
            val Market_Id = (data \ "Market_Id").get.asOpt[String].getOrElse("")
            val query =MongoDBObject("Market_Id" -> Market_Id)
            val dbo = db.getCollection("Market").findOne(query)
            dbo match {
                case None => throw new Exception("warn target does not exist")
                case _ => {
                    val result = toJson(Map("result" -> toJson(Map(
                        "Market_Id" -> toJson(dbo.get.get("Market_Id").asInstanceOf[String]),
                        "Market_Name" -> toJson(dbo.get.get("Market_Name").asInstanceOf[String]))),
                        "status" -> toJson("success")))
                    (successToJson(result), None)
                }
            }
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
        }
    }

    /**
      * 保存市场信息
      *
      * @author liwei
      * @param data
      * @return
      */
    def saveMarket_func(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val db = cm.modules.get.get("db").map (x => x.asInstanceOf[connection_instance]).getOrElse(throw new Exception("no db connection"))
        try {
            val Market_Name = (data \ "Market_Name").get.asOpt[String].getOrElse("")
            val au = (data \ "au").get.asOpt[String].getOrElse("")
            au match {
                case "add" => {
                    val query = MongoDBObject("Market_Name" -> Market_Name)
                    db.getCollection("Market").findOne(query) match {
                        case None => {
                            val doc = MongoDBObject("Market_Id" -> md5(Market_Name),"Market_Name"-> Market_Name,"Date" -> System.currentTimeMillis())
                            db.getCollection("Market").insert(doc).getN match {
                                case 0 => (successToJson(toJson(getErrorMessageByName("warn operation success"))), None)
                                case _ => throw new Exception("warn operation failed")
                            }
                        }
                        case _ => throw new Exception("warn target already exists")
                    }
                }
                case "update" => {
                    val Market_Id = (data \ "Market_Id").get.asOpt[String].getOrElse("")
                    val query = MongoDBObject("Market_Id" -> Market_Id)
                    val update = MongoDBObject("Market_Id" -> md5(Market_Name),"Market_Name" -> Market_Name,"Date" -> System.currentTimeMillis())
                    db.getCollection("Market").update(query,update).getN match {
                        case 1 => (successToJson(toJson(getErrorMessageByName("warn operation success"))), None)
                        case _ => throw new Exception("warn operation failed")
                    }
                }
            }
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage())))
        }
    }
}