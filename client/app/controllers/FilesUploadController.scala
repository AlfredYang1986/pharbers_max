package controllers

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.FilesUploadModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._
/**
	* Created by Wli on 2017/1/4.
	*/
class FilesUploadController extends Controller{

	implicit val cm = CommonModule(Some(Map("" -> None)))

	def scpCopyFiles = Action (request => requestArgs(request) { jv =>
		import pattern.LogMessage.common_log
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("scpCopyFiles"))), jv, request) :: msg_scpCopyFiles(jv) :: msg_CommonResultMessage() :: Nil, None)
	})

	def removeFiles = Action (request => requestArgs(request) { jv =>
		import pattern.LogMessage.common_log
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("removefiles"))), jv, request) :: msg_removeFiles(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
}