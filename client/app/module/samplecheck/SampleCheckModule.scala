package module.samplecheck

import com.pharbers.ErrorCode
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
//import com.pharbers.dbManagerTrait.dbInstanceManager
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
		case MsgQuerySampleSales(data) => querySampleSales(data)(pr)
		case MsgQueryNotSampleHospital(data) => queryNotSampleHospital(data)(pr)
		case _ => throw new Exception("function is not impl")
	}
	
	def querySelectBoxValue(data: JsValue)(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse(throw new Exception("wrong input"))
			val rLst = csv2SampleCheckData(queryPanelWithRedis(uid)).map(x => Map("market" -> x("market"), "date" -> x("date"))).distinct
			(Some(Map("data" -> toJson(Map("selectBox" -> toJson(rLst))) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryHospitalNumber(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
//			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
//			val db = conn.queryDBInstance("calc").get
			
			val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse(throw new Exception("wrong input"))
			
			val market = (data \ "condition" \ "market").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val date = (data \ "condition" \ "date").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			// TODO: 预留对接数据库,可以的话采用MongoDB的MapReduce
 			val timeList = alNearDecemberMonth.diff12Month(date).toList.dropRight(1)
			val rLst = csv2SampleCheckData(queryPanelWithRedis(uid)).toStream.filter(f => f("market") == market && f("date") == date).map(f => f("phaId"))
			val listLst = timeList.map { x =>
				Map("date" -> toJson(x), "hospitalNumber" -> toJson(0))
			} :+ Map("date" -> toJson(date), "hospitalNumber" -> toJson(rLst.distinct.size))
			val reValMap = Map("curHospitalNumber" -> toJson(rLst.distinct.size),
								"preHospitalNumber" -> toJson(0),
								"lastHospitalNumber" -> toJson(0),
								"hospitalList" -> toJson(listLst))
			
			(Some(Map("data" -> toJson(reValMap) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryProductNumber(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
//			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
//			val db = conn.queryDBInstance("calc").get
			
			val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse(throw new Exception("wrong input"))
			
			val market = (data \ "condition" \ "market").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val date = (data \ "condition" \ "date").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			// TODO: 预留对接数据库,可以的话采用MongoDB的MapReduce
			val timeList = alNearDecemberMonth.diff12Month(date).toList.dropRight(1)
			val rLst = csv2SampleCheckData(queryPanelWithRedis(uid)).toStream.filter(f => f("market") == market && f("date") == date).map(f => f("productMini"))
			val lineLst = timeList.map { x =>
				Map("date" -> toJson(x), "productNumber" -> toJson(0))
			} :+ Map("date" -> toJson(date), "productNumber" -> toJson(rLst.distinct.size))
			val reValMap = Map("curProductNumber" -> toJson(rLst.distinct.size),
				"preProductNumber" -> toJson(0),
				"lastProductNumber" -> toJson(0),
				"productList" -> toJson(lineLst))
			
			(Some(Map("data" -> toJson(reValMap) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def querySampleSales(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
//			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
//			val db = conn.queryDBInstance("calc").get
			
			val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse(throw new Exception("wrong input"))
			
			val market = (data \ "condition" \ "market").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val date = (data \ "condition" \ "date").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			// TODO: 预留对接数据库,可以的话采用MongoDB的MapReduce
			val timeList = alNearDecemberMonth.diff12Month(date).toList.dropRight(1)
			val rLst = csv2SampleCheckData(queryPanelWithRedis(uid)).toStream.filter(f => f("market") == market && f("date") == date).map(f => f("sales").toDouble)
			val barLst = timeList.map { x =>
				Map("date" -> toJson(x), "sampleSales" -> toJson(0))
			} :+ Map("date" -> toJson(date), "sampleSales" -> toJson(rLst.sum.formatted("%.2f")))
			val reValMap = Map("sampleSalesList" -> toJson(barLst))
			
			(Some(Map("data" -> toJson(reValMap) )), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryNotSampleHospital(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse(throw new Exception("wrong input"))
			
			val market = (data \ "condition" \ "market").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val date = (data \ "condition" \ "date").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			
			val csvLst = csv2SampleCheckData(queryPanelWithRedis(uid)).toStream.filter(f => f("market") == market && f("date") == date).map(f => f("phaId")).distinct
			val allHospitalLst = xlsx2SampleCheckData(market).toStream.
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
