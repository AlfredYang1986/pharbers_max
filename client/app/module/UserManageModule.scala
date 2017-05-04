package module

import com.mongodb.casbah.commons.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.pharbers.aqll.util.dao._data_connection_basic
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.util.{DateUtils, MD5}
import module.common.alMessage._

object UserManageModuleMessage {
    sealed class msg_UserManageBase extends CommonMessage
    case class msg_usermanage_query(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_delete(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_findOne(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_save(data: JsValue) extends msg_UserManageBase
}

object UserManageModule extends ModuleTrait {
    import UserManageModuleMessage._
    import controllers.common.default_error_handler.f
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_usermanage_query(data) => query_func(data)
        case msg_usermanage_delete(data) => delete_func(data)
        case msg_usermanage_findOne(data) => findOne_func(data)
        case msg_usermanage_save(data) => save_func(data)
    }

    def query_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> toJson(query(data)))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def delete_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("Result" -> toJson("ok"))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def findOne_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("Result" -> toJson("ok"))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def save_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("Result" -> toJson("ok"))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def query(data: JsValue) : List[JsValue] = {
        val companyid = (data \ "company").get.asOpt[String].getOrElse("")
        val query = MongoDBObject("Company_Id" -> companyid)
        val r = _data_connection_basic.getCollection("Company").find(query).toList.head
        val Company_Id = r.get("Company_Id").asInstanceOf[String]
        val E_Mail = r.get("E-Mail").asInstanceOf[String]
        val Company_Name = r.get("Company_Name").asInstanceOf[BasicDBList].toArray.head.asInstanceOf[DBObject]
        val Ch = Company_Name.get("Ch").asInstanceOf[String]
        val En = Company_Name.get("En").asInstanceOf[String]
        val User_lst = r.get("User_lst").asInstanceOf[BasicDBList].toArray
        User_lst.map{x =>
            val user = x.asInstanceOf[DBObject]
            toJson(Map(
                "User_ID" -> toJson(user.get("ID").asInstanceOf[String]),
                "Account" -> toJson(user.get("Account").asInstanceOf[String]),
                "Name" -> toJson(user.get("Name").asInstanceOf[String]),
                "Password" -> toJson(user.get("Password").asInstanceOf[String]),
                "auth" -> toJson(user.get("auth").asInstanceOf[Number].intValue()),
                "isadministrator" -> toJson(user.get("isadministrator").asInstanceOf[Number].intValue() match {
                    case 0 => "普通用户"
                    case _ => "管理员"
                }),
                "Company_Id" -> toJson(Company_Id),
                "E_Mail" -> toJson(E_Mail),
                "Company_Name_Ch" -> toJson(Ch),
                "Company_Name_En" -> toJson(En),
                "Timestamp" -> toJson(DateUtils.Timestamp2yyyyMMdd(user.get("Timestamp").asInstanceOf[Number].longValue()))
            ))
        } toList
    }
}