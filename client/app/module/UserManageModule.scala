package module

import com.pharbers.aqll.pattern.{CommonMessage, CommonModule, MessageDefines, ModuleTrait}
import play.api.libs.json.JsValue
import module.common.{alCompany, alUserManage}
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

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
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm : CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_usermanage_company_query(data) => query_company_func(data)
        case msg_usermanage_company_delete(data) => delete_company_func(data)
        case msg_usermanage_company_findOne(data) => findOne_company_func(data)
        case msg_usermanage_company_save(data) => save_company_func(data)
        case msg_usermanage_user_query(data) => query_user_func(data)
        case msg_usermanage_user_delete(data) => delete_user_func(data)
        case msg_usermanage_user_findOne(data) => findOne_user_func(data)
        case msg_usermanage_user_save(data) => save_user_func(data)
    }

    def query_company_func(data: JsValue)(implicit cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alCompany.queryCompanys(data))),None)
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage)))
        }
    }

    def delete_company_func(data: JsValue)(implicit error_handler: Int => JsValue, cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alCompany.deleteCompany(data))),None)
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage)))
        }
    }

    def findOne_company_func(data: JsValue)(implicit error_handler: Int => JsValue, cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alCompany.findOneCompany(data))),None)
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage)))
        }
    }

    def save_company_func(data: JsValue)(implicit error_handler: Int => JsValue, cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alCompany.saveCompany(data))),None)
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage)))
        }
    }

    def query_user_func(data: JsValue)(implicit error_handler: Int => JsValue, cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alUserManage.queryUsers(data))),None)
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage)))
        }
    }

    def delete_user_func(data: JsValue)(implicit error_handler: Int => JsValue, cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alUserManage.deleteUser(data))),None)
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage)))
        }
    }

    def findOne_user_func(data: JsValue)(implicit error_handler: Int => JsValue, cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alUserManage.findOneUser(data))),None)
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage)))
        }
    }

    def save_user_func(data: JsValue)(implicit error_handler: Int => JsValue, cm: CommonModule): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("result" -> alUserManage.saveUser(data))),None)
        } catch {
            case ex: Exception => (None, Some(errorToJson(ex.getMessage)))
        }
    }
}