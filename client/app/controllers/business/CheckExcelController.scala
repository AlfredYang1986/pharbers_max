package controllers.business

import play.api.mvc._
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.CheckExcelModuleMessage.msg_checkexcel

/**
  * Created by Faiz on 2017/1/5.
  */
object CheckExcelController extends Controller{
    def checkExcelAjaxCall = Action (request => requestArgs(request) { jv =>
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_checkexcel(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}
