package controllers.akkacallback

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.mvc.Action
import play.api.libs.json.Json.toJson
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.message.send.SendMessageTrait
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.akkacallback.AkkaCallBackMessage.MsgAkkaCallBack
import module.auth.AuthMessage.{MsgAuthTokenExpire, MsgAuthTokenParser}

class AkkaCallBackController @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait, msg: SendMessageTrait) {
	implicit val as: ActorSystem = as_inject
	
	def akkaCallBack = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("akkaCallBack"))), jv)
//			:: MsgAuthTokenParser(jv)
//			:: MsgAuthTokenExpire(jv)
			:: MsgAkkaCallBack(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "msg" -> msg))))
	})
}
