package controllers

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.UserManageModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._

class UserManageController extends Controller{
    def userManageQueryAjax = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
      import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageQueryAjax"))), jv, request) :: msg_usermanage_query(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageDeleteAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
      import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageDeleteAjax"))), jv, request) :: msg_usermanage_delete(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageFindOneAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
      import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageFindOneAjax"))), jv, request) :: msg_usermanage_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageSaveAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
      import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageSaveAjax"))), jv, request) :: msg_usermanage_save(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}