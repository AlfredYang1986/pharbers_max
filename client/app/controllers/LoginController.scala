package controllers

import javax.inject.Singleton

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.LoginModuleMessage.msg_login
import play.api.libs.json.Json.toJson
import play.api.mvc._

@Singleton
class LoginController extends Controller{
    implicit val cm = CommonModule(Some(Map("" -> None)))

    def Login = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("Login"))), jv, request) :: msg_login(jv, request.remoteAddress) :: msg_CommonResultMessage() :: Nil, None)
    })
}