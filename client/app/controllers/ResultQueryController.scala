package controllers

import javax.inject.Singleton

import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.ResultQueryModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._
/**
	* Created by Wli on 2017/1/5.
	*/
@Singleton
class ResultQueryController extends Controller{
	implicit val cm = CommonModule(Some(Map("" -> None)))
    
  def resultQueryAjaxCall = Action (request => requestArgs(request) { jv =>
		import pattern.LogMessage.common_log
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("resultQueryAjaxCall"))), jv, request) ::  msg_calc_result_query(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	
}