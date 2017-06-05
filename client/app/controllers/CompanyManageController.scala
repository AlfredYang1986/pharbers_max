package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.aqll.pattern.MessageRoutes
import module.CompanyManageModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._
import controllers.common.requestArgsQuery

class CompanyManageController @Inject()(as_inject : ActorSystem, mdb: MongoDBModule) extends Controller {
    implicit val db = mdb
    implicit val as = as_inject
    def queryCompanys = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryCompanys"))), jv, request) :: msg_companymanage_query(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def deleteCompany = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("deleteCompany"))), jv, request) :: msg_companymanage_delete(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def findOneCompany = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("findOneCompany"))), jv, request) :: msg_companymanage_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def saveCompany = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("saveCompany"))), jv, request) :: msg_companymanage_save(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}