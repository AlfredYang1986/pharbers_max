package controllers.business

import javax.inject.Singleton

import play.api.mvc._
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.LoginModuleMessage.msg_login
import play.api.libs.json.Json.toJson

@Singleton
class LoginController extends Controller{
    def Login = Action(request => requestArgs(request) { jv =>
        import pattern.ResultMessage.common_result
        import pattern.LogMessage.common_log
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("Login"))), jv) :: msg_login(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}