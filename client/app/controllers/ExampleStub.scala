package controllers

import play.api._
import play.api.mvc._

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs

import module.ExampleModuleMessage._

class Example extends Controller {
	def exampAjaxCall = Action (request => requestArgs(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_example_1(jv) :: msg_example_2(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
	def exampAjaxError = Action (request => requestArgs(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_example_1(jv) :: msg_error_emp(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}