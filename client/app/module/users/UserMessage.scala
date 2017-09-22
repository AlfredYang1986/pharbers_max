package module.users

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class msg_UserCommand extends CommonMessage("user", UserModule)
object UserMessage {
    case class msg_user_push(data: JsValue) extends msg_UserCommand
    case class msg_user_delete(data: JsValue) extends msg_UserCommand
    case class msg_user_update(data: JsValue) extends msg_UserCommand
    case class msg_user_query(data: JsValue) extends msg_UserCommand
    case class msg_user_query_info(data: JsValue) extends msg_UserCommand
}
