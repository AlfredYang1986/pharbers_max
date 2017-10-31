package module.register

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by yym on 9/msg_user_register14/17.
  */
abstract class msg_RegisterCommand extends CommonMessage("register", RegisterModule)

object RegisterMessage {
    case class msg_check_user_is_apply(data: JsValue) extends msg_RegisterCommand
    case class msg_user_apply(data: JsValue) extends msg_RegisterCommand
    case class msg_query_apply_user(data: JsValue) extends msg_RegisterCommand
    case class msg_update_apply_user(data: JsValue) extends msg_RegisterCommand
    case class msg_register_token_create(data: JsValue) extends msg_RegisterCommand
    case class msg_approve_reg(data : JsValue) extends msg_RegisterCommand

    case class msg_register_token_defeat(data: JsValue) extends msg_RegisterCommand
    case class msg_delete_registerUser(data : JsValue) extends msg_RegisterCommand
    case class msg_first_push_user(data: JsValue) extends msg_RegisterCommand
}
