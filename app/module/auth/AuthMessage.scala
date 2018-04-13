package module.auth

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class MsgAuthCommand extends CommonMessage("auth", AuthModule)

object AuthMessage {
	case class MsgUserAuth(data: JsValue) extends MsgAuthCommand // msg_user_auth
}