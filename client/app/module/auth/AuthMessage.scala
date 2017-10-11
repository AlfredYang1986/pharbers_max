package module.auth

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class msg_AuthCommand extends CommonMessage("auth", AuthModule)
object AuthMessage {
	case class msg_user_auth(data: JsValue) extends msg_AuthCommand
	case class msg_auth_token_parser(data: JsValue) extends msg_AuthCommand
	case class msg_auth_token_expire(data: JsValue) extends msg_AuthCommand
	case class msg_auth_create_token(data: JsValue) extends msg_AuthCommand
	case class msg_auth_token_push_user(data: JsValue) extends msg_AuthCommand
	case class msg_auth_token_defeat(data: JsValue) extends msg_AuthCommand
	case class msg_auth_code_push_success(data: JsValue) extends msg_AuthCommand
	case class msg_auth_token_type(data: JsValue) extends msg_AuthCommand
	case class msg_auth_token_used(data: JsValue) extends msg_AuthCommand
}