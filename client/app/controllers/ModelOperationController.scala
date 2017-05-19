package controllers

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.ModelOperationModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._

class ModelOperationController extends Controller{
	implicit val cm = CommonModule(Some(Map("" -> None)))

    def mondelOperationBar11AjaxCall = Action (request => requestArgs(request) { jv =>
			import pattern.LogMessage.common_log
			import pattern.ResultMessage.common_result
			MessageRoutes(msg_log(toJson(Map("method" -> toJson("mondelOperationBar11AjaxCall"))), jv, request) :: msg_operationBar11(jv) :: msg_CommonResultMessage() :: Nil, None)
		})

		def mondelOperationBar23AjaxCall = Action (request => requestArgs(request) { jv =>
			import pattern.LogMessage.common_log
			import pattern.ResultMessage.common_result
			MessageRoutes(msg_log(toJson(Map("method" -> toJson("mondelOperationBar23AjaxCall"))), jv, request) :: msg_operationBar23(jv) :: msg_CommonResultMessage() :: Nil, None)
		})
}