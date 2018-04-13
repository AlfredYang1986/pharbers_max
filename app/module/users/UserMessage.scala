package module.users

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class msg_UserCommand extends CommonMessage("users", UserModule)

object UserMessage {
    case class msg_pushUser(data: JsValue) extends msg_UserCommand
    case class msg_popUser(data : JsValue) extends msg_UserCommand
    case class msg_queryUser(data : JsValue) extends msg_UserCommand
    case class msg_queryUserMulti(data : JsValue) extends msg_UserCommand
}