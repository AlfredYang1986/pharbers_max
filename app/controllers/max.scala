package controllers

import javax.inject.Inject
import play.api.mvc.Action
import akka.actor.ActorSystem
import module.jobs.JobMessage._
import play.api.libs.json.Json.toJson
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.users.UserMessage.msg_expendCompanyInfo
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.module.{MAXCallJobPusher, MAXResponseConsumer}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

class max @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait, cp: MAXCallJobPusher, pc: MAXResponseConsumer) {
    implicit val as: ActorSystem = as_inject

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def ymCalc = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("ym calc job"))), jv)
                :: msg_queryJob(jv)
                :: msg_expendUserInfo(jv)
                :: msg_expendCompanyInfoByJob(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_ymCalcJob(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def panel = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("panel job"))), jv)
                :: msg_queryJob(jv)
                :: msg_expendUserInfo(jv)
                :: msg_expendCompanyInfoByJob(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_panelJob(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def calc = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("calc job"))), jv)
                :: msg_queryJob(jv)
                :: msg_expendUserInfo(jv)
                :: msg_expendCompanyInfoByJob(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_calcJob(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def kill = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("kill job"))), jv)
                :: msg_queryJob(jv)
                :: msg_expendUserInfo(jv)
                :: msg_expendCompanyInfoByJob(jv)
                :: msg_expendCompanyInfo(jv)
                :: msg_killJob(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

}
