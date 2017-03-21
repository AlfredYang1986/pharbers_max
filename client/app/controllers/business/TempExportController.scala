package controllers.business

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.TempExportModuleMessage.msg_finalresult1
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller
import play.api.mvc._

/**
  * Created by Wli on 2017/2/13 0013.
  */
class TempExportController extends Controller{
    def tempExportAjaxCall = Action (request => requestArgs(request) { jv =>
        import pattern.ResultMessage.common_result
        import pattern.LogMessage.common_log
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("tempExportAjaxCall"))), jv, request) :: msg_finalresult1(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}
