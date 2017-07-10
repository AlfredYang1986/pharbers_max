package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery
import module.PageInitializationModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

/**
  * Created by liwei on 2017/6/5.
  */
class PageInitializationController@Inject()(as_inject : ActorSystem, mdb: MongoDBModule) extends Controller {
    import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result

    implicit val db = mdb
    implicit val as = as_inject
    def loadPageData = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("loadPageData"))), jv, request) :: msg_loadPageData(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}
