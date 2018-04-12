package controllers.history

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.auth.AuthMessage._
import module.history.HistoryMessage._
import module.users.UserMessage.msg_user_token_op
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

class HistoryController @Inject () (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller {
	implicit val as = as_inject
	
	def queryHistorySelect = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryHistorySelect"))), jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
			:: MsgQueryHistorySelect(jv)
			:: msg_CommonResultMessage() :: Nil, None)(
			CommonModules(Some(Map("db" -> dbt, "att" -> att))))
		
	})
	
	def queryHistoryByPage = Action(request => requestArgsQuery().requestArgsV2(request) {jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryHistoryByPage"))), jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
			:: MsgQueryHistoryCount(jv)
			:: MsgQueryHistoryByPage(jv)
			:: msg_CommonResultMessage() :: Nil, None)(
			CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
}
