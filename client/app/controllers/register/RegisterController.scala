package controllers.register

import javax.inject.Inject

import akka.actor.ActorSystem
import module.register.RegisterMessage._
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.auth.AuthMessage.{msg_auth_token_expire, msg_auth_token_parser, msg_auth_token_type}
import module.users.UserMessage.msg_check_user_is_register
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

class RegisterController @Inject () (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller {
	implicit val as = as_inject

	def user_apply = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("user_apply"))), jv)
				:: msg_check_user_is_register(jv)
				:: msg_check_user_is_apply(jv)
				:: msg_user_apply(jv)
				:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def query_apply_user = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("query_apple_bd"))), jv)
			:: msg_auth_token_parser(jv)
			:: msg_auth_token_expire(jv)
			:: msg_auth_token_type(jv)
			:: msg_query_apply_user(jv)
			:: msg_CommonResultMessage() :: Nil, None)(
				CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def update_apply_user = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("update_apply_user"))), jv)
			:: msg_auth_token_parser(jv)
			:: msg_auth_token_expire(jv)
			:: msg_auth_token_type(jv)
			:: msg_update_apply_user(jv)
			:: msg_CommonResultMessage() :: Nil, None)(
			CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})

}
