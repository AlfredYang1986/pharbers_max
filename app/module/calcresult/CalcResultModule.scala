package module.calcresult

import java.util.UUID
import module.calcresult.CalcResultData.CalcResultData
import module.calcresult.CalcResultMessage._
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json._
import com.mongodb.casbah.Imports._
import module.common.alNearDecemberMonth
import scala.collection.immutable.Map

import com.pharbers.ErrorCode
import com.pharbers.aqll.common.MergeParallelResult
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.common.datatype.date.PhDateOpt

// TODO: 这次记住 要重构，已经看不下去了
object CalcResultModule extends ModuleTrait with CalcResultData {
	
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		
		case MsgCalcResultCondition(data) => queryCalcResultConditions(data)(pr)
		
		case MsgCalcResultSalesVsShare(data) => queryCalcResultCurSalesVsShare(data)(pr)
		case MsgCalcResultHistorySumSales(data) => queryCalcResultHistory(data)(pr)
		case MsgCalcResultAreaData(data) => queryAreaData(data)(pr)
		case MsgCalcResultCurVsPreWithCity(data) => queryCalcResultAreaData(data)(pr)
		case _ => throw new Exception("function is not impl")
	}

	
	def queryCalcResultHistory(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val o = shareMrHistoryBeforeCondition(data, pr.get)
			val tempTable = UUID.randomUUID().toString//"CalcResultShareHistory"
			val reVal = db.mapReduceJob(pr.get("user_company").as[String], shareMapJs, shareReduceJs, Some(o), tempTable)
			if (reVal) {
				val reVal = db.queryMultipleObject(shareMrHistoryAfterCondition(data, pr.get), tempTable, "_id", 0, Int.MaxValue)(shareDataToMap)
				(Some(Map("history" -> toJson(reVal)) ++ pr.get), None)
			} else {
				(None, Some(ErrorCode.errorToJson("data not exist")))
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryAreaData(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val o = areaMrHistoryBeforeCondition(data, pr.get)
			val tempTable = UUID.randomUUID().toString//CalcResultAreaHistory
			val reVal = db.mapReduceJob(pr.get("user_company").as[String], areaCityMapJs, areaCityReduceJs, Some(o), tempTable)
			if (reVal) {
				val reVal = db.queryMultipleObject(areaMrHistoryAfterCondition(data, pr.get), tempTable, "_id", 1, Int.MaxValue)(areaDataToMap)
				(Some(Map("history" -> toJson(reVal)) ++ pr.get), None)
//				(Some(Map("pre_result" -> toJson("")) ++ pr.get ++ data.as[JsObject].value.toMap), None)
			} else {
				(None, Some(ErrorCode.errorToJson("data not exist")))
			}
			
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResultCurSalesVsShare(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val o = shareMrCurrentBeforeCondition(data, pr.get)
			val tempTable = UUID.randomUUID().toString//CalcResultShareCur
			val reVal = db.mapReduceJob((data \ "condition" \ "table").asOpt[String].getOrElse(""), shareMapJs, shareReduceJs, Some(o), tempTable)
			if (reVal) {
				val reVal = db.queryMultipleObject(shareMrCurrentAfterCondition(data, pr.get), tempTable, "_id", 0, Int.MaxValue)(shareDataToMap)
				(Some(Map("cur" -> toJson(reVal)) ++
					Map("selectMarket" -> toJson(o.getAs[String]("Market").get),
						"selectDate" -> toJson(o.getAs[Number]("Date").get.longValue()) ) ), None)
			} else {
				(None, Some(ErrorCode.errorToJson("data not exist")))
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResultAreaData(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val o = areaMrCurrentBeforeCondition(data, pr.get)
			val tempTable = UUID.randomUUID().toString//CalcResultAreaCur
			val reVal = db.mapReduceJob((data \ "condition" \ "table").asOpt[String].getOrElse(""), areaCityMapJs, areaCityReduceJs, Some(o), tempTable)
			if (reVal) {
				val reVal = db.queryMultipleObject(areaMrCurrentAfterCondition(data, pr.get), tempTable, "_id", 0, Int.MaxValue)(areaDataToMap)
				(Some(Map("cur" -> toJson(reVal)) ++
					Map("selectMarket" -> toJson(o.getAs[String]("Market").get),
						"selectDate" -> toJson(o.getAs[Number]("Date").get.longValue() ) ) ), None)
			} else {
				(None, Some(ErrorCode.errorToJson("data not exist")))
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	
	def queryCalcResultConditions(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market"))
			val table = (data \ "condition" \ "table").asOpt[String].getOrElse("")
			val reVal = db.aggregate(DBObject(), table, group)(aggregateConditionResult)
			(reVal, None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def salesVsShareResultMerge(lst: List[Map[String, JsValue]])
	                           (pr: Option[Map[String, JsValue]]): Map[String, JsValue] = {
		
		
		val mergerResult = MergeParallelResult(lst)
		val selectDate = PhDateOpt.Timestamp2yyyyMM(mergerResult("selectDate").as[Long])
		val selectMarket = mergerResult("selectMarket").as[String]
		val show_company = pr.get("show_company").asOpt[String] match {
			case None => ""
			case Some(one) => one
		}
		val timeLst = alNearDecemberMonth.diff12Month(selectDate).toList.dropRight(1)

		val curTemp = mergerResult("cur").as[List[String Map String]]
		val historyTemp = pr.get("history").as[List[String Map String]]
		
		val lastYear = PhDateOpt.Timestamp2yyyyMM(PhDateOpt.yyyyMM2LastLong(selectDate))// 去年同期时间戳
		
		val curPSumS = (curTemp.filter(f => f("Product").contains(show_company)).map(x => x("Sales").toDouble).sum / 1000000).formatted("%.2f").toDouble
		val curMSumS = (curTemp.map(x => x("Sales").toDouble).sum / 1000000).formatted("%.2f").toDouble
		val curMSumU = (curTemp.map(x => x("Units").toDouble).sum / 1000000).formatted("%.2f").toDouble
		val share = if(curMSumS == 0) 0 else ((curPSumS / curMSumS) * 100).formatted("%.2f").toDouble
		val result = historyTemp match {
			case Nil =>
 				timeLst.filterNot(f => f == selectDate).map ( x =>Map("Date" -> x, "Market" -> selectMarket, "Sales" -> "0", "Units" -> "0", "Share" ->"0")) :+
				Map("Date" -> selectDate, "Market" -> selectMarket, "Sales" -> curMSumS.toString, "Units" -> curMSumU.toString, "Share" -> share.toString)
			case hlst =>
				timeLst.map{ x =>
					val hisPSumS = (hlst.filter(f => f("Date") == x && f("Product").contains(show_company)).map(z => z("Sales").toDouble).sum / 1000000).formatted("%.2f").toDouble
					val hisMSumS = (hlst.filter(f => f("Date") == x).map(z => z("Sales").toDouble).sum / 1000000).formatted("%.2f").toDouble
					val hisMSumU = (hlst.filter(f => f("Date") == x).map(z => z("Units").toDouble).sum / 1000000).formatted("%.2f").toDouble
					val share = if(hisMSumS == 0) 0 else ((hisPSumS / hisMSumS) * 100).formatted("%.2f").toDouble
					Map("Date" -> x, "Market" -> selectMarket , "Sales" -> hisMSumS.toString, "Units" -> hisMSumU.toString, "Share" -> share.toString)
				} :+ Map("Date" -> selectDate, "Market" -> selectMarket, "Sales" -> curMSumS.toString, "Units" -> curMSumU.toString, "Share" -> share.toString)
		}
		val currentDataYear = historyTemp match {
			case Nil => Map("sales_year" -> toJson("0"),
							"productSales_year" -> toJson("0"))
			case hlst =>
				val salesSum = (hlst.filter(f => f("Date") == lastYear).map(z => z("Sales").toDouble).sum / 1000000).formatted("%.2f").toDouble
				val productSalesSum = (hlst.filter(f => f("Date") == lastYear && f("Product").contains(show_company)).map(z => z("Sales").toDouble).sum / 1000000).formatted("%.2f").toDouble
				
				println(curMSumS)
				println(curPSumS)
				
				println(salesSum)
				println(productSalesSum)
				
				Map("sales_year" -> toJson {
						if(salesSum == 0) "0"
						else (((curMSumS - salesSum) / salesSum) * 100).formatted("%.2f")
					},
					"productSales_year" -> toJson {
						if(productSalesSum == 0) "0"
						else (((curPSumS - productSalesSum) / productSalesSum) * 100).formatted("%.2f")
					}
				)
		}
		
		println(lastYear)
		println(currentDataYear)
		
		Map("condition" -> toJson(result),
			"data_last" -> toJson(currentDataYear),
			"result_condition" -> (pr.get("result_condition") \ "select_values").getOrElse(throw new Exception("")),
			"cursales" -> toJson(curMSumS),
			"selectDate" -> toJson(selectDate),
			"selectMarket" -> toJson(selectMarket),
			"curproductsales" -> toJson(curPSumS)
		   )
	}
	
	def salesMapWithCityResultMerge(lst: List[Map[String, JsValue]])(pr: Option[Map[String, JsValue]]): Map[String, JsValue] = {
		
		def provincesResultData(key: String,
		                        gData: String Map Stream[String Map String],
		                        date: String, market: String,
		                        showCompany: String): Stream[String Map String] = {
			gData.map { x =>
				val sumSales = (x._2.map(f => f("Sales").toDouble).sum / 1000000).formatted("%.2f").toDouble
				val sumUnits = (x._2.map(f => f("Units").toDouble).sum / 1000000).formatted("%.2f").toDouble
				val sumProductSales = (x._2.filter(f => f("Product").contains(showCompany)).map(f => f("Sales").toDouble).sum / 1000000).formatted("%.2f").toDouble
				val share =  if(sumSales == 0) 0 else ((sumProductSales / sumSales) * 100).formatted("%.2f").toDouble
				Map("Date" -> date, "Market" -> market, key -> x._1,
					"Sales" -> sumSales.toString, "Units" -> sumUnits.toString,
					"ProductSales" -> sumProductSales.toString, "Share" -> share.toString )
			}.toStream
		}
		
		val mergerResult = MergeParallelResult(lst)
		
		val selectDate = PhDateOpt.Timestamp2yyyyMM(mergerResult("selectDate").as[Long])
		val selectMarket = mergerResult("selectMarket").as[String]
		val show_company = pr.get("show_company").asOpt[String] match {
			case None => ""
			case Some(one) => one
		}
		val curDataTemp = mergerResult("cur").as[List[String Map String]].toStream
		
		val curProvincesData = provincesResultData("Provinces", curDataTemp.groupBy(g => g("Provinces")),
			selectDate, selectMarket, show_company).
			sortBy(s => s("Sales").toDouble).reverse
		
		val historyProvincesData = curProvincesData.take(10).toList.flatMap { x =>
			val history = pr.get("history").as[List[String Map String]].toStream
			provincesResultData("Provinces", history.groupBy(g => g("Provinces")).filter(f => f._1 == x("Provinces")), selectDate, selectMarket, show_company)
		}
		
		val curCityData = provincesResultData("City", curDataTemp.groupBy(g => g("City")),
			selectDate, selectMarket, show_company).
			sortBy(s => s("Sales").toDouble).reverse.take(10).toList
		
		val historyCityData = curCityData.flatMap { x =>
			val history = pr.get("history").as[List[String Map String]].toStream
			provincesResultData("City", history.groupBy(g => g("City")).filter(f => f._1 == x("City")), selectDate, selectMarket, show_company)
		}
		
		val provincesData = Map("cur_provinces" -> toJson(curProvincesData.take(10).toList), "history_provinces" -> toJson(historyProvincesData))
		val cityData = Map("cur_city" -> toJson(curCityData), "history_city" -> toJson(historyCityData))
		
		Map("condition" -> toJson(curProvincesData),
			"provinces_bar" -> toJson(provincesData),
			"city_bar" -> toJson(cityData)
			)
	}
	
	def conditionResultMerge(lst: List[Map[String, JsValue]])
	                        (pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): Map[String, JsValue] = {
		try {
			val para = MergeParallelResult(lst)
			val temp = para.values.map(x => x.as[List[String Map JsValue]]).toList.flatten
			val user = pr.get("user").as[JsObject].value.toMap
			Map("result_condition" -> toJson(Map("select_values" -> toJson(temp)))) ++ Map("user_company" -> user("company"), "show_company" -> user("showCompany"))
		} catch {
			case ex: Exception => ???
		}
		
	}
	
	
}
