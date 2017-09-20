package bmlogic.register

import com.pharbers.bmmessages.CommonMessage
import module.register.RegisterModule
import play.api.libs.json.JsValue

/**
  * Created by yym on 9/14/17.
  */
abstract class msg_RegisterCommand extends CommonMessage("register", RegisterModule)

object RegisterMessage {
    case class msg_pushAdminCommand(data : JsValue) extends msg_RegisterCommand
    case class msg_pushRegisterWithoutCheckCommand(data : JsValue) extends msg_RegisterCommand
    case class msg_queryRegisterWithIDCommand(data : JsValue) extends msg_RegisterCommand
    case class msg_queryAllRegistersCommand(data : JsValue) extends msg_RegisterCommand
    case class msg_checkRegisterStatusCommand(data : JsValue) extends msg_RegisterCommand
    case class msg_deleteRegisterCommand(data : JsValue) extends msg_RegisterCommand
    case class msg_cryptRegisterCodeCommand(data : JsValue) extends msg_RegisterCommand
    case class msg_decryptRegisterCodeCommand(data : JsValue) extends msg_RegisterCommand
    case class msg_checkAuthTokenExpireCommand(data : JsValue) extends msg_RegisterCommand
}
