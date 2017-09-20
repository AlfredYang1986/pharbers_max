package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.pharbers.mongodbDriver.DBTrait
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.LoginModuleMessage.msg_login
import play.api.libs.json.Json.toJson
import play.api.mvc._

@Singleton
class LoginController@Inject()(as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject
    
    def Login = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("Login"))), jv) :: msg_login(jv, request.remoteAddress) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
}