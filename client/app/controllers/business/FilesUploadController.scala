package controllers.business

import play.api.mvc._
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.FilesUploadModuleMessage._
import play.api.libs.json.Json.toJson
/**
	* Created by Wli on 2017/1/4.
	*/
class FilesUploadController extends Controller{
    
	def filesUploadAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.ResultMessage.common_result
		import pattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("filesUploadAjaxCall"))), jv, request) :: msg_filesupload(jv) :: msg_CommonResultMessage() :: Nil, None)
	})

	def filesexistsAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.ResultMessage.common_result
		import pattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("filesexistsAjaxCall"))), jv, request) :: msg_filesexists(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
}