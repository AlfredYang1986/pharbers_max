package controllers

import javax.inject.Inject

import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.PageInitializationModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

/**
  * Created by liwei on 2017/6/5.
  */
class PageInitializationController@Inject()(mdb: MongoDBModule) extends Controller {
    import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result

    implicit val db = mdb

    def loadPageData = Action(request => requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("loadPageData"))), jv, request) :: msg_loadPageData(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}
