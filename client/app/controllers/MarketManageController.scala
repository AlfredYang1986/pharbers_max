package controllers

import javax.inject.Inject

import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.MarketManageModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._

class MarketManageController@Inject() (mdb: MongoDBModule) extends Controller{
    implicit val dbc = mdb.basic

    implicit val cm = CommonModule(Some(Map("db" -> dbc)))

    def queryMarkets = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryMarkets"))), jv, request) :: msg_marketmanage_query(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def deleteMarket = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("deleteMarket"))), jv, request) :: msg_marketmanage_delete(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def findOneMarket = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("findOneMarket"))), jv, request) :: msg_marketmanage_findOne(jv) :: msg_CommonResultMessage() :: Nil, None)
    })

    def saveMarket = Action(request => requestArgs(request) { jv =>
        import pattern.LogMessage.common_log
        import pattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("saveMarket"))), jv, request) :: msg_marketmanage_save(jv) :: msg_CommonResultMessage() :: Nil, None)
    })
}