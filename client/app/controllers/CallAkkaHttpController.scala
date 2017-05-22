package controllers

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.CallAkkaHttpModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{Controller, _}

/**
  * Created by qianpeng on 2017/2/13.
  */
class CallAkkaHttpController extends Controller{
    implicit val cm = CommonModule(Some(Map("" -> None)))

    import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result

    def callHttpServer = Action (request => requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("callHttpServer"))), jv, request) :: msg_callHttpServer(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}
