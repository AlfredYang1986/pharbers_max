package controllers.business

import play.api._
import play.api.mvc._

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs

import module.business.ResultQueryModuleMessage._

object ResultQueryStub extends Controller{
    
    def resultQueryAjaxCall = Action (request => requestArgs(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_provincedata(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
//	def resultQueryAjaxError = Action (request => requestArgs(request) { jv => 
//		import pattern.ResultMessage.common_result
//		MessageRoutes(msg_provincedata(jv) :: msg_citydata(jv) :: msg_hospitaldata(jv) :: msg_error_emp(jv) :: msg_CommonResultMessage() :: Nil, None)
//	})
}