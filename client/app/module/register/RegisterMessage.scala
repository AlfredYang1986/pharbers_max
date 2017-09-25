package bmlogic.register

import com.pharbers.bmmessages.CommonMessage
import module.register.RegisterModule
import play.api.libs.json.JsValue

/**
  * Created by yym on 9/14/17.
  */
abstract class msg_RegisterCommand extends CommonMessage("register", RegisterModule)

object RegisterMessage {
    case class msg_user_register(data: JsValue) extends msg_RegisterCommand
    case class msg_query_register_bd(data: JsValue) extends msg_RegisterCommand
}
