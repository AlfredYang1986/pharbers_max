package controllers

import javax.inject.Inject

import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import controllers.common.requestArgsQuery.requestArgs
import module.CompanyManageModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._

class CompanyManageController @Inject()(mdb: MongoDBModule) extends Controller {
    implicit val dbc = mdb.basic

    implicit val cm = CommonModule(Some(Map("db" -> dbc)))

    def queryCompanys = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryCompanys"))), jv, request) :: msg_companymanage_query(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def deleteCompany = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("deleteCompany"))), jv, request) :: msg_companymanage_delete(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def findOneCompany = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("findOneCompany"))), jv, request) :: msg_companymanage_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def saveCompany = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("saveCompany"))), jv, request) :: msg_companymanage_save(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}