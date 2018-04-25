package controllers

import javax.inject.Inject

import play.api.mvc.Action
import akka.actor.ActorSystem
import module.jobs.JobMessage._
import play.api.libs.json.Json.toJson
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.users.UserMessage.msg_queryUser
import com.pharbers.module.MAXMsgChannelModule
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage

class jobs @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait, channel: MAXMsgChannelModule) {
    implicit val as: ActorSystem = as_inject
    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def pushJob() = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("push new job"))), jv)
            :: msg_queryUser(jv) // 检查创建job的用户
            :: msg_pushJob(jv) // 创建一个job
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


    def panelJob = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("panel job"))), jv)
            :: msg_panelJob(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "channel" -> channel))))
    })

    def calcJob = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("calc job"))), jv)
            :: msg_calcJob(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "channel" -> channel))))
    })

    def killJob = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("kill job"))), jv)
            :: msg_killJob(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "channel" -> channel))))
    })

}
