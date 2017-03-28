package controllers

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.ModelOperationModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._

class ModelOperationController extends Controller{
    def mondelOperationAjaxCall = Action (request => requestArgs(request) { jv =>
			import pattern.LogMessage.common_log
			import pattern.ResultMessage.common_result
			MessageRoutes(msg_log(toJson(Map("method" -> toJson("mondelOperationAjaxCall"))), jv, request) :: msg_operation(jv) :: msg_CommonResultMessage() :: Nil, None)
		})
}