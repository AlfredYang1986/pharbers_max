package controllers

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.FilesUploadModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._
/**
	* Created by Wli on 2017/1/4.
	*/
class FilesUploadController extends Controller{
	def scpFileAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.LogMessage.common_log
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("classifyFilesAjaxCall"))), jv, request) :: msg_scpfile(jv) :: msg_CommonResultMessage() :: Nil, None)
	})

	def removefilesAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.LogMessage.common_log
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("removefilesAjaxCall"))), jv, request) :: msg_removefiles(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
}