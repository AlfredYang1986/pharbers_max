package controllers

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import javax.inject.Inject
import module.common.em.EmMessage.{msg_registerUserForEm, msg_userJoinChatgroupForEm}
import module.company.CompanyMessage.msg_queryRegisterCompany
import module.users.UserMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.Action

class users @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait, rd: PhRedisDriverImpl) {
    implicit val as: ActorSystem = as_inject

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def pushUser = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("push new user"))), jv)
                :: msg_verifyUserRegister(jv)
                :: msg_queryRegisterCompany(jv)
                :: msg_pushUser(jv)
                :: msg_bindUserCompanyPre(jv)
                :: msg_bindUserCompany(jv)
                :: msg_registerUserForEm(jv)
                :: msg_userJoinChatgroupForEm(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def popUser = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("pop user"))), jv)
                :: msg_pushUser(jv)
                :: msg_popUser(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def queryUser = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query user"))), jv)
                :: msg_queryUser(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def queryUserMulti = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query user multi"))), jv)
                :: msg_queryUserMulti(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def userDetail = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("user detail"))), jv)
                :: msg_queryUser(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def userJobs = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("user jobs"))), jv)
                :: msg_queryUser(jv)
                :: msg_expendJobsInfo(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def userRoles = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("user roles"))), jv)
                :: msg_queryUser(jv)
                :: msg_expendRolesInfo(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def userLogin = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query user multi"))), jv)
                :: msg_authWithPassword(jv)
                :: msg_authSetExpire(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "rd" -> rd))))
    })
}
