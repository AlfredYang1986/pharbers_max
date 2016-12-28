package controllers.business

import play.api.mvc._
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.LoginModuleMessage.msg_login

object LoginController extends Controller{
    def Login = Action(request => requestArgs(request) { jv =>
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_login(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}