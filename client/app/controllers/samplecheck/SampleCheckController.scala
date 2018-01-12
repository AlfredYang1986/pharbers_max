package controllers.samplecheck

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.samplecheck.SampleCheckMessage._
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

class SampleCheckController @Inject() (as_inject: ActorSystem, dbt: dbInstanceManager, att: AuthTokenTrait) extends Controller {
	implicit val as: ActorSystem = as_inject
	
	def querySelectBoxValue = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("querySelectBoxValue"))), jv)
			:: MsgQuerySelectBoxValue(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def queryDataBaseLine = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryDataBaseLine"))), jv)
			:: MsgQueryDataBaseLine(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def queryHospitalNumber = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryHospitalNumber"))), jv)
			:: MsgQueryHospitalNumber(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def queryProductNumber = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryProductNumber"))), jv)
			:: MsgQueryProductNumber(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def querySampleSales = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("querySampleSales"))), jv)
			:: MsgQuerySampleSales(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})

	def queryHospitalList = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryHospitalList"))), jv)
			:: MsgQueryNotSampleHospital(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
}
