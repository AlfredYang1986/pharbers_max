package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import module.SampleCheckModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._
import controllers.common.requestArgsQuery

class SampleCheckController @Inject()(as_inject : ActorSystem, mdb: MongoDBModule) extends Controller {
    implicit val db = mdb
    implicit val as = as_inject
    def sampleCheck = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("sampleCheck"))), jv, request) :: msg_samplecheck(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}