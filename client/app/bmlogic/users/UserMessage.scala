package bmlogic.users

import bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by apple on 9/15/17.
  */
abstract class msg_UserCommand extends CommonMessage
object UserMessage {
    case class msg_PushUserCommand(data : JsValue) extends msg_UserCommand
    case class msg_queryUserWithIDCommand(data : JsValue) extends msg_UserCommand
    case class msg_queryAllUsersCommand(data : JsValue) extends msg_UserCommand
    case class msg_deleteUserCommand(data : JsValue) extends msg_UserCommand
    
}
