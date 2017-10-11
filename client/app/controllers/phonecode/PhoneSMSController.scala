package controllers.phonecode

import javax.inject.Inject

import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import akka.actor.ActorSystem
import play.api.mvc._
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery
import module.auth.AuthMessage.{msg_auth_token_expire, msg_auth_token_parser}
import module.phonecode.PhoneCodeMessages._
import play.api.libs.json.Json.toJson

class PhoneSMSController @Inject () (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) {
	implicit val as = as_inject
	
	def sendSMSCode = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.ResultMessage.common_result
		import com.pharbers.bmpattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("send sms code"))), jv) :: msg_check_send_time(jv) ::  msg_send_sms_code(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def checkSMSCode = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.ResultMessage.common_result
		import com.pharbers.bmpattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("check sms code"))), jv) :: msg_check_sms_code(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
}
