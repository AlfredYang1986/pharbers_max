package module.register

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by yym on 9/msg_user_register14/17.
  */
abstract class msg_RegisterCommand extends CommonMessage("register", RegisterModule)

object RegisterMessage {
    case class msg_is_user_register(data: JsValue) extends msg_RegisterCommand
    case class msg_user_register(data: JsValue) extends msg_RegisterCommand
    case class msg_query_register_bd(data: JsValue) extends msg_RegisterCommand
    case class msg_approve_reg(data : JsValue) extends msg_RegisterCommand
    case class msg_user_filter_register(data: JsValue) extends msg_RegisterCommand
    case class msg_user_register_status(data: JsValue) extends msg_RegisterCommand
}
