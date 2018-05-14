package controllers

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.module.{MAXCallJobPusher, MAXResponseConsumer}
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import javax.inject.Inject
import module.jobs.JobMessage.{msg_expendUserInfo, _}
import module.users.UserMessage.msg_queryUser
import play.api.libs.json.Json.toJson
import play.api.mvc.Action

class jobs @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait, cp: MAXCallJobPusher, rc: MAXResponseConsumer) {
    implicit val as: ActorSystem = as_inject

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def pushJob() = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("push new job"))), jv)
                :: msg_queryUser(jv) // 检查创建job的用户
                :: msg_pushJob(jv) // 创建一个job
                :: msg_bindJobUserPre(jv)
                :: msg_bindJobUser(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def popJob = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("pop job"))), jv)
                :: msg_popJob(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def queryJob = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query job"))), jv)
                :: msg_queryJob(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def queryJobMulti = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("query job multi"))), jv)
                :: msg_queryJobMulti(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def jobDetail = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("job detail"))), jv)
                :: msg_queryJob(jv)
                :: msg_expendUserInfo(jv)
                :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
}
