package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import module.LoginModuleMessage.msg_login
import play.api.libs.json.Json.toJson
import play.api.mvc._
import controllers.common.requestArgsQuery

@Singleton
class LoginController@Inject()(as_inject : ActorSystem, mdb: MongoDBModule) extends Controller {
    implicit val db = mdb
    implicit val as = as_inject
    def Login = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("Login"))), jv, request) :: msg_login(jv, request.remoteAddress) :: msg_CommonResultMessage() :: Nil, None)
    })
}