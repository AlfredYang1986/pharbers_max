package controllers.manage

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.manage.ManageFilesUploadModuleMessage._
import play.api.mvc._

object FilesUploadController extends Controller{
    
    def filesUploadAjaxCall = Action (request => requestArgs(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_managefilesupload(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
}