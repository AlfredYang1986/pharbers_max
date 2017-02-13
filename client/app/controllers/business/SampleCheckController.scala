package controllers.business

import play.api.mvc._
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.SampleCheckModuleMessage.msg_samplecheck
import pattern.LogMessage.msg_log
import play.api.libs.json.Json.toJson

class SampleCheckController extends Controller{
    
    def sampleCheckAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.ResultMessage.common_result
		import pattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("sampleCheckAjaxCall"))), jv) :: msg_samplecheck(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}