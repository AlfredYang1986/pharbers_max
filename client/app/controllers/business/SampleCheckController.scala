package controllers.business

import play.api.mvc._
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.SampleCheckModuleMessage._
import pattern.LogMessage.msg_log
import play.api.libs.json.Json.toJson

class SampleCheckController extends Controller{
    
    def sampleCheckAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.ResultMessage.common_result
		import pattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("sampleCheckAjaxCall"))), jv, request) :: msg_samplecheck(jv) :: msg_samplecheckyesteryear(jv) :: msg_samplecheckyestermonth(jv) :: msg_CommonResultMessage() :: Nil, None)
	})

	def sampleCheckAjaxChartsLineCall = Action(request => requestArgs(request) { jv =>
		import pattern.ResultMessage.common_result
		import pattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("sampleCheckAjaxChartsCall"))), jv, request) :: msg_samplecheck(jv) :: msg_samplechecktopline(jv) :: msg_CommonResultMessage() :: Nil, None)
	})

	def sampleCheckAjaxChartsPlotCall = Action(request => requestArgs(request) { jv =>
		import pattern.ResultMessage.common_result
		import pattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("sampleCheckAjaxChartsPlotCall"))), jv, request) :: msg_samplecheck(jv) :: msg_samplecheckplot(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}