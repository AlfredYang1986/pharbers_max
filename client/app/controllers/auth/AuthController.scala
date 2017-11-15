package controllers.auth

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.message.send.SendMessageTrait
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.auth.AuthMessage._
import module.register.RegisterMessage._
import module.users.UserMessage.{msg_check_user_is_register, msg_user_token_op}
import play.api.libs.json.Json.toJson
import play.api.mvc.Action

class AuthController @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait, msg: SendMessageTrait) {
	implicit val as = as_inject
	
	def auth_with_password = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth_with_password"))), jv)
			:: MsgUserAuth(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	def auth_create_token = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth_create_token"))), jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: MsgAuthTokenType(jv)
			:: msg_check_user_is_register(jv)
			:: msg_check_user_is_apply(jv)
			:: msg_register_token_create(jv)
			:: MsgAuthCreateToken(jv)
			:: msg_approve_reg(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	def auth_token_push_user = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth_token_push_user"))), jv)
			:: MsgAuthTokenUsed(jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
			:: MsgAuthCodePushSuccess(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
	
	def auth_token_defeat = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth_token_defeat"))), jv)
			:: msg_check_user_is_register(jv)
			:: msg_check_user_is_apply(jv)
			:: msg_register_token_defeat(jv)
			:: MsgAuthCreateToken(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
}
