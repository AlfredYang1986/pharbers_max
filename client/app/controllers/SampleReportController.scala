package controllers

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.SampleReportModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._

class SampleReportController extends Controller{

  implicit val cm = CommonModule(Some(Map("" -> None)))

  def sampleReportAjaxCall = Action (request => requestArgs(request) { jv =>
    import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result
    MessageRoutes(msg_log(toJson(Map("method" -> toJson("sampleCheckAjaxCall"))), jv, request) :: msg_samplereport(jv) :: msg_CommonResultMessage() :: Nil, None)
  })
}