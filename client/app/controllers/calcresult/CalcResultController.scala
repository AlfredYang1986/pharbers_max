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
import module.auth.AuthMessage._
import module.calcresult.CalcResultMessage._
import module.calcresult.CalcResultModule._
import module.users.UserMessage.msg_user_token_op
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.Action

class CalcResultController @Inject() (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) {
	implicit val as: ActorSystem = as_inject
	implicit val commonModules: CommonModules = CommonModules(Some(Map("db" -> dbt, "att" -> att)))
	
	def querySalesVsShare =  Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryCalcResult"))), jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
//			::
			:: ParallelMessage(paralleCondition(jv), conditionResultMerge)
			:: MsgCalcResultHistorySumSales(jv)
			:: ParallelMessage(paralleSalesVsShare(jv), salesVsShareResultMerge)
			:: msg_CommonResultMessage() :: Nil, None)
	})
	
	def queryCurVsPreWithCity = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryCurVsPreWithCity"))), jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
			//			::
			:: ParallelMessage(paralleCondition(jv), conditionResultMerge)
			:: MsgCalcResultHistoryCurVsPreWithCity(jv)
			:: ParallelMessage(paralleCurVsPreWithCity(jv), curVsPreWithCity)
			:: msg_CommonResultMessage() :: Nil, None)
	})
	
	def queryWithYearForCurVsPre = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryWithYearForCurVsPre"))), jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
			//			::
			:: ParallelMessage(paralleCondition(jv), conditionResultMerge)
			:: MsgCalcResultHistoryWithYearForCurVsPre(jv)
			:: ParallelMessage(paralleWithYearForCurVsPre(jv), withYeaForCurVsPre)
			:: msg_CommonResultMessage() :: Nil, None)
	})
	
	
	def paralleSalesVsShare(jv: JsValue): List[MessageRoutes] = {
		val reVal = (jv \ "condition" \ "tables").asOpt[List[String]].map(x => x).getOrElse(throw new Exception(""))
		val js = (jv \ "condition").asOpt[String Map JsValue].map(x => x - "tables").getOrElse(throw new Exception(""))
		reVal map ( x => MessageRoutes(MsgCalcResultSalesVsShare(toJson(Map("condition" -> toJson(Map("table" -> toJson(x)) ++ js )))) :: Nil, None))
	}
	
	def paralleCurVsPreWithCity(jv: JsValue): List[MessageRoutes] = {
		val reVal = (jv \ "condition" \ "tables").asOpt[List[String]].map(x => x).getOrElse(throw new Exception(""))
		val js = (jv \ "condition").asOpt[String Map JsValue].map(x => x - "tables").getOrElse(throw new Exception(""))
		reVal map ( x => MessageRoutes(MsgCalcResultCurVsPreWithCity(toJson(Map("condition" -> toJson(Map("table" -> toJson(x)) ++ js )))) :: Nil, None))
	}
	
	def paralleWithYearForCurVsPre(jv: JsValue): List[MessageRoutes] = {
		val reVal = (jv \ "condition" \ "tables").asOpt[List[String]].map(x => x).getOrElse(throw new Exception(""))
		val js = (jv \ "condition").asOpt[String Map JsValue].map(x => x - "tables").getOrElse(throw new Exception(""))
		reVal map ( x => MessageRoutes(MsgCalcResultWithYearForCurVsPre(toJson(Map("condition" -> toJson(Map("table" -> toJson(x)) ++ js )))) :: Nil, None))
	}
	
	def paralleCondition(jv: JsValue): List[MessageRoutes] = {
		val reVal = (jv \ "condition" \ "tables").asOpt[List[String]].map(x => x).getOrElse(throw new Exception(""))
		val js = (jv \ "condition").asOpt[String Map JsValue].map(x => x - "tables").getOrElse(throw new Exception(""))
		reVal map ( x => MessageRoutes(MsgCalcResultCondition(toJson(Map("condition" -> toJson(Map("table" -> toJson(x)) ++ js )))) :: Nil, None))
	}
}
