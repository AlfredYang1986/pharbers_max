package controllers

import javax.inject.Inject
import com.pharbers.aqll.dbmodule.MongoDBModule
import com.pharbers.aqll.pattern
import com.pharbers.aqll.pattern.LogMessage.msg_log
import com.pharbers.aqll.pattern.{CommonModule, MessageRoutes}
import com.pharbers.aqll.pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery.requestArgs
import module.ResultCheckModuleMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc._

class ResultCheckController@Inject() (mdb: MongoDBModule) extends Controller{
	implicit val dbc = mdb.cores

	implicit val cm = CommonModule(Some(Map("db" -> dbc)))

    def resultChecklinechart = Action (request => requestArgs(request) { jv =>
			import pattern.LogMessage.common_log
			import pattern.ResultMessage.common_result
			MessageRoutes(msg_log(toJson(Map("method" -> toJson("resultChecklinechart"))), jv, request) :: msg_linechart(jv) :: msg_CommonResultMessage() :: Nil, None)
		})

		def resultCheckhistogram = Action (request => requestArgs(request) { jv =>
			import pattern.LogMessage.common_log
			import pattern.ResultMessage.common_result
			MessageRoutes(msg_log(toJson(Map("method" -> toJson("resultCheckhistogram"))), jv, request) :: msg_histogram(jv) :: msg_CommonResultMessage() :: Nil, None)
		})
}