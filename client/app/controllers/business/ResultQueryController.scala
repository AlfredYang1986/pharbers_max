package controllers.business

import play.api._
import play.api.mvc._

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs

import module.business.ResultQueryModuleMessage._

object ResultQueryController extends Controller{
    
    def resultQueryAjaxCall = Action (request => requestArgs(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_finalresult(jv) :: msg_hospitalresult(jv) :: msg_miniproductresult(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
}