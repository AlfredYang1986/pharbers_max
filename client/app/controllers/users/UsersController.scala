package controllers.users

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.users.UserMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

class UsersController @Inject () (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller {
	implicit val as = as_inject
	
	def user_push = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("user_push"))), jv) :: msg_user_push(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def user_delete = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("user_delete"))), jv) :: msg_user_delete(jv) ::  msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def user_update = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("user_update"))), jv) :: msg_user_update(jv) ::  msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def user_query = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("user_query"))), jv) :: msg_user_query(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def user_query_info = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("user_query_info"))), jv) :: msg_user_query_info(jv) ::  msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
}
