package controllers.business

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import module.business.FileExportModuleMessage._
import controllers.common.requestArgsQuery.requestArgs
import play.api.mvc._

object FileExportController extends Controller{
    
    def fileExportAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_fileexport(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
}