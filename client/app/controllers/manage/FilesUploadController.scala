package controllers.manage

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.manage.ManageFilesUploadModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._

class FilesUploadController extends Controller{
    
    def filesUploadAjaxCall = Action (request => requestArgs(request) { jv => 
		import pattern.ResultMessage.common_result
		import pattern.LogMessage.common_log
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("manageFilesUploadAjaxCall"))), jv) :: msg_managefilesupload(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
}