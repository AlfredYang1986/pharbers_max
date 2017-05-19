package controllers

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.MarketManageModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._

class MarketManageController extends Controller{
    implicit val cm = CommonModule(Some(Map("" -> None)))

    def marketManageQueryAjax = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("marketManageQueryAjax"))), jv, request) :: msg_marketmanage_query(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def marketManageDeleteAjax = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("marketManageDeleteAjax"))), jv, request) :: msg_marketmanage_delete(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def marketManageFindOneAjax = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("marketManageFindOneAjax"))), jv, request) :: msg_marketmanage_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def marketManageSaveAjax = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("marketManageSaveAjax"))), jv, request) :: msg_marketmanage_save(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}