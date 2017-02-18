package controllers.business

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.ModelOperationModuleMessage._
import play.api.mvc._
import pattern.LogMessage.msg_log
import play.api.libs.json.Json.toJson

class ModelOperationController extends Controller{
    def mondelOperationAjaxCall = Action (request => requestArgs(request) { jv =>
			import pattern.ResultMessage.common_result
			import pattern.LogMessage.common_log
			MessageRoutes(msg_log(toJson(Map("method" -> toJson("mondelOperationAjaxCall"))), jv) :: msg_operation(jv) :: msg_CommonResultMessage() :: Nil, None)
		})
}