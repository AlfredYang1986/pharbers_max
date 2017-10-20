package controllers.calcresult

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ParallelMessage
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.calcresult.CalcResultMessage._
import module.calcresult.CalcResultModule._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.Action

class CalcResultController @Inject() (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) {
	implicit val as = as_inject
	implicit val commonModules = CommonModules(Some(Map("db" -> dbt, "att" -> att)))
	
	def queryCalcResult =  Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryCalcResult"))), jv)
			:: ParallelMessage(paralleCondition(jv), conditionResultMerge)
			:: ParallelMessage(paralleCollections(jv), detailResultMerge)
			:: msg_CommonResultMessage() :: Nil, None)
	})
	
	
	def paralleCollections(jv: JsValue): List[MessageRoutes] = {
		val reVal = (jv \ "condition" \ "tables").asOpt[List[String]].map(x => x).getOrElse(throw new Exception(""))
		reVal map ( x => MessageRoutes(MsgCalcResult(toJson(Map("condition" -> toJson(Map("table" -> x))))) :: Nil, None))
	}
	
	def paralleCondition(jv: JsValue): List[MessageRoutes] = {
		val reVal = (jv \ "condition" \ "tables").asOpt[List[String]].map(x => x).getOrElse(throw new Exception(""))
		reVal map ( x => MessageRoutes(MsgCalcResultCondition(toJson(Map("condition" -> toJson(Map("table" -> x))))) :: Nil, None))
	}
}
