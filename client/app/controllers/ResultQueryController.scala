package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.MessageRoutes
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import module.ResultQueryModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._
import controllers.common.requestArgsQuery

class ResultQueryController@Inject() (as_inject : ActorSystem, mdb: MongoDBModule) extends Controller{
	implicit val db = mdb
	implicit val as = as_inject
  def resultQuerySearch = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
		import pattern.LogMessage.common_log
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("resultQuerySearch"))), jv, request) ::  msg_resultquery(jv) :: msg_CommonResultMessage() :: Nil, None)
	})

}