package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import module.CompanyManageModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._
import controllers.common.requestArgsQuery

class CompanyManageController @Inject()(as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject
    
    def queryCompanys = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryCompanys"))), jv) :: msg_companymanage_query(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def deleteCompany = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("deleteCompany"))), jv) :: msg_companymanage_delete(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def findOneCompany = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("findOneCompany"))), jv) :: msg_companymanage_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def saveCompany = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import com.pharbers.bmpattern.LogMessage.common_log
        import com.pharbers.bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("saveCompany"))), jv) :: msg_companymanage_save(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
}