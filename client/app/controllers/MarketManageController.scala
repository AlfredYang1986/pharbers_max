package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.db.DBTrait
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.token.AuthTokenTrait
import module.MarketManageModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._
import controllers.common.requestArgsQuery

class MarketManageController @Inject()(as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject
    import com.pharbers.bmpattern.LogMessage.common_log
    import com.pharbers.bmpattern.ResultMessage.common_result
    def queryMarkets = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryMarkets"))), jv) :: msg_marketmanage_query(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def deleteMarket = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("deleteMarket"))), jv) :: msg_marketmanage_delete(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def findOneMarket = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("findOneMarket"))), jv) :: msg_marketmanage_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def saveMarket = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("saveMarket"))), jv) :: msg_marketmanage_save(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
}