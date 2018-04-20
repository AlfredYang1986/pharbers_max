package controllers.binding

import javax.inject.Inject
import play.api.mvc.Action
import akka.actor.ActorSystem
import module.jobs.JobMessage._
import play.api.libs.json.Json.toJson
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import module.users.UserMessage.{msg_expendJobsInfo, msg_queryUser}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

class bindJobUser @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait) {
    implicit val as: ActorSystem = as_inject
    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def bind = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("bind job user"))), jv)
            :: msg_verifyUserBind(jv)
            :: msg_bindJobUser(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def unbind = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("unbind job user"))), jv)
            :: msg_unbindJobUser(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def jobDetail = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("job detail"))), jv)
            :: msg_queryJob(jv)
            :: msg_expendUserInfo(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def userJobs = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("user jobs"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendJobsInfo(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
}

