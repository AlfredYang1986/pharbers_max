package module.auth

import com.pharbers.bmmessages.CommonMessage
import play.api.libs.json.JsValue

abstract class MsgAuthCommand extends CommonMessage("auth", AuthModule)
object AuthMessage {
	case class MsgUserAuth(data: JsValue) extends MsgAuthCommand // msg_user_auth
	case class MsgAuthTokenParser(data: JsValue) extends MsgAuthCommand // msg_auth_token_parser
	case class MsgAuthTokenExpire(data: JsValue) extends MsgAuthCommand // msg_auth_token_expire
	case class MsgAuthCreateToken(data: JsValue) extends MsgAuthCommand // msg_auth_create_token
	case class MsgAuthTokenPushUser(data: JsValue) extends MsgAuthCommand // msg_auth_token_push_user
	case class MsgAuthCodePushSuccess(data: JsValue) extends MsgAuthCommand // msg_auth_code_push_success
	case class MsgAuthTokenType(data: JsValue) extends MsgAuthCommand // msg_auth_token_type
	case class MsgAuthTokenUsed(data: JsValue) extends MsgAuthCommand // msg_auth_token_used
	case class MsgAuthCheckTokenAction(data: JsValue) extends MsgAuthCommand // msg_auth_check_token_action
}