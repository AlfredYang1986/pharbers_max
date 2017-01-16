package controllers.business

import play.api.mvc._
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.business.CheckExcelModuleMessage.msg_checkexcel
import com.pharbers.aqll.pattern.LogMessage.msg_log
import play.api.libs.json.Json.toJson
/**
  * Created by Faiz on 2017/1/2.
  */
class CheckExcelController extends Controller{
    def checkExcelAjaxCall = Action (request => requestArgs(request) { jv =>
        import pattern.ResultMessage.common_result
        import pattern.LogMessage.common_log
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("checkExcelAjaxCall"))), jv) :: msg_checkexcel(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}
