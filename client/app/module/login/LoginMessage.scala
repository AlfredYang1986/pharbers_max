package module.login

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class msg_LoginCommand extends CommonMessage("user", LoginModule)
object LoginMessage {
	
	case class msg_user_login(data: JsValue) extends msg_LoginCommand
	
}