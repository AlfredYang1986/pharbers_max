package controllers.business

import play.api.mvc._
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.SampleCheckModuleMessage.msg_samplecheck

object SampleCheckImpl extends Controller{
    
    def sampleCheckAjaxCall = Action (request => requestArgs(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_samplecheck(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}