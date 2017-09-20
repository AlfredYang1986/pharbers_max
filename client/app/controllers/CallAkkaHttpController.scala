package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.mongodbDriver.DBTrait
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.token.AuthTokenTrait
import module.CallAkkaHttpModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{Controller, _}
import controllers.common.requestArgsQuery

class CallAkkaHttpController@Inject()(as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
//    implicit val db = mdb
    implicit val as = as_inject
    def callHttpServer = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("callHttpServer"))), jv) :: msg_callHttpServer(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
}
