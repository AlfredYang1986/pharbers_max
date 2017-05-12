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
    def userManageCompanyQueryAjax = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
      import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageCompanyQueryAjax"))), jv, request) :: msg_usermanage_company_query(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageCompanyDeleteAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
      import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageCompanyDeleteAjax"))), jv, request) :: msg_usermanage_company_delete(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageCompanyFindOneAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
      import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageCompanyFindOneAjax"))), jv, request) :: msg_usermanage_company_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageCompanySaveAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
      import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageCompanySaveAjax"))), jv, request) :: msg_usermanage_company_save(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageUserQueryAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageUserQueryAjax"))), jv, request) :: msg_usermanage_user_query(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageUserDeleteAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageUserDeleteAjax"))), jv, request) :: msg_usermanage_user_delete(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageUserFindOneAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageUserFindOneAjax"))), jv, request) :: msg_usermanage_user_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def userManageUserSaveAjax = Action(request => requestArgs(request) { jv =>
      import pattern.LogMessage.common_log
    import pattern.ResultMessage.common_result
      MessageRoutes(msg_log(toJson(Map("method" -> toJson("userManageUserSaveAjax"))), jv, request) :: msg_usermanage_user_save(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}