package controllers

import play.api._
import play.api.mvc._
import com.pharbers.aqll.pattern.MessageRoutes

import controllers.common.requestArgsQuery._

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.aqll.pattern.ParallelMessage
import com.pharbers.aqll.pattern.ParallelMessage.f
import module.test.testMessages._

object SatterGatherTestController extends Controller {
	def testIndex = Action (request => requestArgs(request) { jv => 
			import pattern.ResultMessage.common_result
			MessageRoutes(
					ParallelMessage(
							MessageRoutes(msg_Test_1(jv) :: msg_Test_3(jv) :: Nil, None) :: 
							MessageRoutes(msg_Test_2(jv) :: Nil, None) :: Nil) 
					:: msg_Test_3(jv)
					:: msg_CommonResultMessage() :: Nil, None)
		})
}