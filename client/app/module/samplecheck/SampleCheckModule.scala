package module.samplecheck

import com.mongodb.casbah.Imports._
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

object SampleCheckModule extends ModuleTrait with SampleData {
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgQuerySelectBoxValue(data) => querySelectBoxValue(data)
		case MsgQueryDataBaseLine(data) => queryDataBaseLine(data)
		case MsgQueryHospitalNumber(data) => queryHospitalNumber(data)(pr)
		case MsgQueryProductNumber(data) => queryProductNumber(data)(pr)
		case MsgQuerySampleSales(data) => querySampleSales(data)(pr)
		case MsgQueryNotSampleHospital(data) => queryNotSampleHospital(data)(pr)
		case _ => throw new Exception("function is not impl")
	}
	
	def querySelectBoxValue(data: JsValue)(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse(throw new Exception("wrong input"))
			val company = (data \ "condition" \ "company").asOpt[String].getOrElse(throw new Exception("wrong input"))
			val rLst = csv2SampleCheckData(queryPanelWithRedis(uid), company).map(x => Map("market" -> x("market"), "date" -> x("date"))).distinct
			(Some(Map("data" -> toJson(Map("selectBox" -> toJson(rLst))) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryDataBaseLine(data: JsValue)(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val company = (data \ "condition" \ "company").asOpt[String].getOrElse(throw new Exception("wrong input"))
			val o = condition(data)
			val reVal = db.queryMultipleObject(o, s"${company}_BaseLine", "Month").sortBy(s => s("Month").as[Int])
			(Some(Map("data" -> toJson(Map("baseLine" -> toJson(reVal))) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryHospitalNumber(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			(Some(Map("data" -> toJson(createEchartsData(data, "phaId")) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryProductNumber(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			(Some(Map("data" -> toJson(createEchartsData(data, "productMini")) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def querySampleSales(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			(Some(Map("data" -> toJson(createEchartsData(data, "sales", CalcSum())) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryNotSampleHospital(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse(throw new Exception("wrong input"))
			val company = (data \ "condition" \ "company").asOpt[String].getOrElse(throw new Exception("wrong input"))
			val market = (data \ "condition" \ "market").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val date = (data \ "condition" \ "date").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			
			val csvLst = csv2SampleCheckData(queryPanelWithRedis(uid), company).toStream.filter(f => f("market") == market && f("date") == date).map(f => f("phaId")).distinct
			val allHospitalLst = xlsx2SampleCheckData(market, company).toStream.
				filter(f => f("If Panel_All") == "1").
				map(x => x - "住院病人手术人次数" - "住院治疗收入" - "门诊西药收入"
							- "医疗收入" - "住院收入" - "住院药品收入"
							- "If Panel_To Use" - "外科诊次" - "Re-Speialty"
							- "眼科床位数" - "If Panel_All" - "Pane诊药品收入"
							- "Factor" - "Specialty_1" - "年"
							- "内科床位数" - "门诊收入" - "入院人数"
							- "Specialty 3" - "医生数" - "全科床位数"
							- "If County" - "年诊疗人次" - "药品收入"
							- "床位数" - "住院手术收入" - "内科诊次"
							- "门诊手术收入" - "Specialty_2" - "公司"
							- "住院床位收入" - "西药收入" - "门诊诊次"
							- "住院西药收入" - "门诊治疗收入" - "外科床位数" - "门诊药品收入" - "Segment")
			
			val r = allHospitalLst.map{ x =>
				if(!csvLst.contains(x("PHA ID"))) Some(x)
				else None
			}.filter(_.isDefined)
			(Some(Map("data" -> toJson(r) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
}
