package controllers.business

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.ModelOperationModuleMessage._
import play.api.mvc._

object ModelOperationController extends Controller{
    def mondelOperationAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.ResultMessage.common_result
		println("测试成功")
		MessageRoutes(msg_operation(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}