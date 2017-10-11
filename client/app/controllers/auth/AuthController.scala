package controllers.auth

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.auth.AuthMessage._
import module.register.RegisterMessage._
import module.users.UserMessage.{msg_user_not_exist, msg_user_token_op}
import play.api.libs.json.Json.toJson
import play.api.mvc.Action

class AuthController @Inject () (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait){
	implicit val as = as_inject
	
	def auth_with_password = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth_with_password"))), jv) :: msg_user_auth(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def auth_create_token = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth_create_token"))), jv)
			:: msg_auth_token_parser(jv)
			:: msg_auth_token_expire(jv)
			:: msg_auth_token_type(jv)
			:: msg_user_not_exist(jv)
			:: msg_is_user_register(jv)
			:: msg_register_token_create(jv)
			:: msg_auth_create_token(jv)
			:: msg_approve_reg(jv)
			:: msg_CommonResultMessage() :: Nil, None)(
				CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})

	def auth_token_push_user = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth_token_push_user"))), jv)
			:: msg_auth_token_used(jv)
            :: msg_auth_token_parser(jv)
			:: msg_auth_token_expire(jv)
            :: msg_user_token_op(jv)
            :: msg_auth_code_push_success(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def auth_token_defeat = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth_token_defeat"))), jv)
			:: msg_register_token_defeat(jv)
            :: msg_auth_token_defeat(jv)
            :: msg_auth_create_token(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
}
