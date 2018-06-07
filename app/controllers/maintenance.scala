package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.module.{MAXCallJobPusher, MAXResponseConsumer}
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.maintenance.MaintenanceMessage._
import module.users.UserMessage.{msg_expendCompanyInfo, msg_queryUser}
import play.api.libs.json.Json.toJson
import play.api.mvc.Action

class maintenance @Inject()(as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait, cp: MAXCallJobPusher, rc: MAXResponseConsumer) {
    implicit val as: ActorSystem = as_inject

    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result

    def getAllCompanies = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("get maintenance all companies"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_getMaintenanceCenterAllCompanies(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def getDataCleanModuleAllFiles = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("get DataCleanModule all files"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_getDataCleanModuleAllFiles(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def getSimpleModuleAllFiles = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("get SimpleModule all files"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_getSimpleModuleAllFiles(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def getMaxModuleAllFiles = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("get MaxModule all files"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_getMaxModuleAllFiles(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def getDeliveryModuleAllFiles = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("get DeliveryModule all files"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_getDeliveryModuleAllFiles(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def replaceDataCleanModuleFile = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("replace DataCleanModule file"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_replaceDataCleanModuleFile(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def replaceSimpleModuleFile = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("replace DataCleanModule file"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_replaceSimpleModuleFile(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def replaceMaxModuleFile = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("replace MaxModule file"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_replaceMaxModuleFile(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

    def replaceDeliveryModuleFile = Action(request => requestArgsQuery().requestArgs(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("replace DeliveryModule file"))), jv)
            :: msg_queryUser(jv)
            :: msg_expendCompanyInfo(jv)
            :: msg_replaceDeliveryModuleFile(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "cp" -> cp))))
    })

}
