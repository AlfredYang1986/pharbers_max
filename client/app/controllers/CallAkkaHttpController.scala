package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import module.CallAkkaHttpModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{Controller, _}
import controllers.common.requestArgsQuery

class CallAkkaHttpController@Inject()(as_inject : ActorSystem, mdb: MongoDBModule) extends Controller {
    import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result
    implicit val db = mdb
    implicit val as = as_inject
    def callHttpServer = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("callHttpServer"))), jv, request) :: msg_callHttpServer(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}
