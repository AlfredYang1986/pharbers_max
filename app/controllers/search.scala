package controllers

import play.api.mvc.Action
import javax.inject.Inject
import akka.actor.ActorSystem
import module.jobs.SearchMessage._
import play.api.libs.json.Json.toJson
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.module.{MAXCallJobPusher, MAXResponseConsumer}
import module.users.UserMessage.{msg_expendCompanyInfo, msg_queryUser}
import module.jobs.JobMessage.{msg_expendCompanyInfoByJob, msg_expendUserInfo, msg_queryJob}

class search @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait, cp: MAXCallJobPusher, rc: MAXResponseConsumer) {
    implicit val as: ActorSystem = as_inject

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def history = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("search history data"))), jv)
                :: msg_queryUser(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_searchHistory(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def exportData = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("search history data"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_searchExportData(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def exportDelivery = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("search history data"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_searchExportDelivery(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def market = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("search company market"))), jv)
                :: msg_queryUser(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_searchAllMkt(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def simpleCheckSelect = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("search simple check"))), jv)
                :: msg_queryJob(jv)
                :: msg_expendUserInfo(jv)
                :: msg_expendCompanyInfoByJob(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_searchSimpleCheckSelect(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def simpleCheck = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("search simple check"))), jv)
                :: msg_queryJob(jv)
                :: msg_expendUserInfo(jv)
                :: msg_expendCompanyInfoByJob(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_searchSimpleCheck(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def resultCheck = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("search result check"))), jv)
                :: msg_queryJob(jv)
                :: msg_expendUserInfo(jv)
                :: msg_expendCompanyInfoByJob(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_searchResultCheck(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

}
