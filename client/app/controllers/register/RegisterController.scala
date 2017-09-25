package controllers.register

import javax.inject.Inject

import akka.actor.ActorSystem
import bmlogic.register.RegisterMessage._
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

class RegisterController @Inject () (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller {
	implicit val as = as_inject
	
	def user_register = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("user_register"))), jv) :: msg_user_register(jv) ::  msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def query_register_bd = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("query_register_bd"))), jv) :: msg_query_register_bd(jv) ::  msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
}
