package controllers


import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import module.UserManageModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._
import controllers.common.requestArgsQuery

class UserManageController@Inject()(as_inject : ActorSystem, mdb: MongoDBModule) extends Controller {
    implicit val db = mdb
    implicit val as = as_inject
    def queryUsers = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryUsers"))), jv, request) :: msg_usermanage_query(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def deleteUser = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("deleteUser"))), jv, request) :: msg_usermanage_delete(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def findOneUser = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("findOneUser"))), jv, request) :: msg_usermanage_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def saveUser = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("saveUser"))), jv, request) :: msg_usermanage_save(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}