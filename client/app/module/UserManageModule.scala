package module

import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.{alCompany,alUserManage}

object UserManageModuleMessage {
    sealed class msg_UserManageBase extends CommonMessage
    case class msg_usermanage_company_query(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_company_delete(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_company_findOne(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_company_save(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_user_query(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_user_delete(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_user_findOne(data: JsValue) extends msg_UserManageBase
    case class msg_usermanage_user_save(data: JsValue) extends msg_UserManageBase
}

object UserManageModule extends ModuleTrait {
    import UserManageModuleMessage._
    import controllers.common.default_error_handler.f
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_usermanage_company_query(data) => query_company_func(data)
        case msg_usermanage_company_delete(data) => delete_company_func(data)
        case msg_usermanage_company_findOne(data) => findOne_company_func(data)
        case msg_usermanage_company_save(data) => save_company_func(data)
        case msg_usermanage_user_query(data) => query_user_func(data)
        case msg_usermanage_user_delete(data) => delete_user_func(data)
        case msg_usermanage_user_findOne(data) => findOne_user_func(data)
        case msg_usermanage_user_save(data) => save_user_func(data)
    }

    def query_company_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alCompany.query(data))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def delete_company_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alCompany.delete(data))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def findOne_company_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alCompany.findOne(data))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def save_company_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alCompany.save(data))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def query_user_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alUserManage.query(data))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def delete_user_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alUserManage.delete(data))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def findOne_user_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alUserManage.findOne(data))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }

    def save_user_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alUserManage.save(data))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }
}