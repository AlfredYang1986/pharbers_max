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
    
	def filesUploadAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.LogMessage.common_log
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("filesUploadAjaxCall"))), jv, request) :: msg_filesupload(jv) :: msg_CommonResultMessage() :: Nil, None)
	})

	def filesexistsAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.LogMessage.common_log
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("filesexistsAjaxCall"))), jv, request) :: msg_filesexists(jv) :: msg_CommonResultMessage() :: Nil, None)
	})

	def classifyFilesAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.LogMessage.common_log
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("classifyFilesAjaxCall"))), jv, request) :: msg_classifyfiles(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
}