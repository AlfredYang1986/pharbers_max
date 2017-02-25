package controllers.business

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import module.business.FileExportModuleMessage._
import controllers.common.requestArgsQuery.requestArgs
import play.api.mvc._
import pattern.LogMessage.msg_log
import play.api.libs.json.Json.toJson
/**
	* Created by Wli on 2017/1/3.
	*/
class FileExportController extends Controller{

    def fileExportAjaxCall = Action (request => requestArgs(request) { jv =>
			import pattern.ResultMessage.common_result
			import pattern.LogMessage.common_log
			MessageRoutes(msg_log(toJson(Map("method" -> toJson("fileExportAjaxCall"))), jv,request) :: msg_finalresult1(jv) :: msg_CommonResultMessage() :: Nil, None)
//			MessageRoutes(msg_log(toJson(Map("method" -> toJson("fileExportAjaxCall"))), jv) :: msg_finalresult1(jv) :: msg_finalresult2(jv) :: msg_finalresult3(jv) :: msg_expotresult1(jv) :: msg_CommonResultMessage() :: Nil, None)
		})

}