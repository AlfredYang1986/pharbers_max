package controllers.calcresult

import javax.inject.Inject

import akka.actor.ActorSystem
import com.pharbers.bmmessages.{CommonModules, MessageRoutes}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ParallelMessage
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.token.AuthTokenTrait
import controllers.common.requestArgsQuery
import module.auth.AuthMessage._
import module.calcresult.CalcResultMessage._
import module.calcresult.CalcResultModule._
import module.users.UserMessage.msg_user_token_op
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.Action

trait redisController {
	def getRedisCollections(uid: String): List[String] = {
		val rd = phRedisDriver().commonDriver
		val rid = rd.hget(uid, "rid").map(x=>x).getOrElse(throw new Exception("not found uid"))
		val panelLst = rd.smembers(rid).map(x=>x.map(_.get)).getOrElse(throw new Exception("panel list is none"))
		panelLst.map(panel =>rd.hget(panel, "tid").getOrElse(throw new Exception("not found tid"))).toList
	}
	
	def paralleSalesVsShare(jv: JsValue)(implicit cm: CommonModules): List[MessageRoutes] = {
		val uid = (jv \ "condition" \ "uid").asOpt[String].map(x => x).getOrElse(throw new Exception(""))
		val company = phRedisDriver().commonDriver.hget(uid, "company").get
		val js = (jv \ "condition").asOpt[String Map JsValue].map(x => x).getOrElse(throw new Exception(""))
		getRedisCollections(uid).map { x =>
			MessageRoutes(MsgCalcResultSalesVsShare(toJson(Map("condition" -> toJson(Map("table" -> toJson(s"$company$x")) ++ js)))) :: Nil, None)
		}
	}
	
	def paralleCurVsPreWithCity(jv: JsValue)(implicit cm: CommonModules): List[MessageRoutes] = {
		val uid = (jv \ "condition" \ "uid").asOpt[String].map(x => x).getOrElse(throw new Exception(""))
		val company = phRedisDriver().commonDriver.hget(uid, "company").get
		val js = (jv \ "condition").asOpt[String Map JsValue].map(x => x).getOrElse(throw new Exception(""))
		getRedisCollections(uid).map { x =>
			MessageRoutes(MsgCalcResultCurVsPreWithCity(toJson(Map("condition" -> toJson(Map("table" -> toJson(s"$company$x")) ++ js )))) :: Nil, None)
		}
	}

	def paralleCondition(jv: JsValue)(implicit cm: CommonModules): List[MessageRoutes] = {
		val uid = (jv \ "condition" \ "uid").asOpt[String].map(x => x).getOrElse(throw new Exception(""))
		val company = phRedisDriver().commonDriver.hget(uid, "company").get
		val js = (jv \ "condition").asOpt[String Map JsValue].map(x => x).getOrElse(throw new Exception(""))
		getRedisCollections(uid).map ( x => MessageRoutes(MsgCalcResultCondition(toJson(Map("condition" -> toJson(Map("table" -> toJson(s"$company$x")) ++ js )))) :: Nil, None))
	}
}

class CalcResultController @Inject() (as_inject : ActorSystem, dbt : dbInstanceManager, att : AuthTokenTrait) extends redisController {
	implicit val as: ActorSystem = as_inject
	implicit val commonModules: CommonModules = CommonModules(Some(Map("db" -> dbt, "att" -> att)))
	
	def querySelectBoxValue =  Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("querySelectBoxResult"))), jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
			:: ParallelMessage(paralleCondition(jv), conditionResultMerge)
			:: msg_CommonResultMessage() :: Nil, None)
	})
	
	def querySalesVsShare =  Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import com.pharbers.bmpattern.LogMessage.common_log
		import com.pharbers.bmpattern.ResultMessage.common_result
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryCalcResult"))), jv)
			:: MsgAuthTokenParser(jv)
			:: MsgAuthTokenExpire(jv)
			:: msg_user_token_op(jv)
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
			:: ParallelMessage(paralleCondition(jv), conditionResultMerge)
			:: MsgCalcResultHistoryCurVsPreWithCity(jv)
			:: ParallelMessage(paralleCurVsPreWithCity(jv), salesMapWithCityResultMerge)
			:: msg_CommonResultMessage() :: Nil, None)
	})
	
}
