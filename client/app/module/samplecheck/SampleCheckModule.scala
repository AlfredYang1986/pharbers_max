package module.samplecheck

import com.pharbers.ErrorCode
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.common.alNearDecemberMonth
import module.samplecheck.SampleCheckMessage._
import module.samplecheck.SampleData._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map

object SampleCheckModule extends ModuleTrait with SampleData{
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgQuerySelectBoxValue(data) => querySelectBoxValue(data)
		case MsgQueryHospitalNumber(data) => queryHospitalNumber(data)(pr)
		case MsgQueryProductNumber(data) => queryProductNumber(data)(pr)
		case MsgQuerySampleProductNumber(data) => querySampleProductNumber(data)(pr)
		case MsgQueryNotSampleHospital(data) => queryNotSampleHospital(data)(pr)
		case _ => throw new Exception("function is not impl")
	}
	
	def querySelectBoxValue(data: JsValue)(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val file = (data \ "condition" \ "filename").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val rLst = csv2SampleCheckData(file).map(x => Map("market" -> x("market"), "date" -> x("date"))).distinct
			(Some(Map("data" -> toJson(Map("selectBox" -> toJson(rLst))) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryHospitalNumber(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			
			val file = (data \ "condition" \ "filename").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val market = (data \ "condition" \ "market").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val date = (data \ "condition" \ "date").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			// TODO: 预留对接数据库
 			val timeList = alNearDecemberMonth.diff12Month(date).toList.dropRight(1)
			println(timeList)
			
			val rLst = csv2SampleCheckData(file).toStream.filter(f => f("market") == market && f("date") == date).map(f => f("phaId"))
			val reValMap = Map("curHospitalNumber" -> toJson(rLst.distinct.size),
								"preHospitalNumber" -> toJson(0),
								"lastHospitalNumber" -> toJson(0),
								"hospitalList" -> toJson(""))
			
			(Some(Map("data" -> toJson(reValMap) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryProductNumber(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			
			(Some(Map("" -> toJson("") )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def querySampleProductNumber(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			
			(Some(Map("" -> toJson("") )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryNotSampleHospital(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			
			(Some(Map("" -> toJson("") )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
}
