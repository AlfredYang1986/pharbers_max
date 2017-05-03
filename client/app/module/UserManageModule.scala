package module

import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import play.api.libs.json.Json._
import play.api.libs.json._

object UserManageModuleMessage {
    sealed class msg_UserManageBase extends CommonMessage
    case class msg_usermanage_query(data: JsValue) extends msg_UserManageBase
}

object UserManageModule extends ModuleTrait {
    import UserManageModuleMessage._
    import controllers.common.default_error_handler.f
    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_usermanage_query(data) => query(data)
    }

    def query(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            (Some(Map("Result" -> toJson("ok"))),None)
        } catch {
            case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
        }
    }
}