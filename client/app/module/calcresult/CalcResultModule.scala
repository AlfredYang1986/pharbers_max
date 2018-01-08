package module.calcresult

import com.pharbers.ErrorCode
import com.pharbers.aqll.common.MergeParallelResult
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.calcresult.CalcResultData.CalcResultData
import module.calcresult.CalcResultMessage._
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json._
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alDate.java.DateUtil
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import com.pharbers.sercuity.Sercurity
import module.common.alNearDecemberMonth

import scala.collection.immutable.Map

// TODO: 这次记住 要重构，已经看不下去了
object CalcResultModule extends ModuleTrait with CalcResultData {
	
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgCalcResultHistorySumSales(data: JsValue) => queryCalcResultHistory(data)(pr)
		case MsgCalcResultHistoryCurVsPreWithCity(data: JsValue) => queryCalcResultHistoryCurVsPreWithCity(data)(pr)
		
		case MsgCalcResultCondition(data: JsValue) => queryCalcResultConditions(data)(pr)
		
		case MsgCalcResultCurVsPreWithCity(data: JsValue) => queryCalcResultCurVsShare(data)(pr)
		case MsgCalcResultSalesVsShare(data: JsValue) => queryCalcResultSalesVsShare(data)(pr)
		case _ => throw new Exception("function is not impl")
	}

	def queryCalcResultHistory(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market", "Product" -> "$Product"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
			
			val para = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Date" -> DBObject("$lt" -> DateUtil.getDateLong(default(pr).getOrElse("Date", throw new Exception("")))), "Market" -> default(pr).getOrElse("Market", ""))
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Date" -> DBObject("$lt" -> DateUtil.getDateLong(tmp.head)), "Market" -> tmp.tail.head)
			}
			val uid = Sercurity.md5Hash(default(pr).getOrElse("Date", "") + para.getAs[String]("Market").get)
			val sumSales = db.aggregate(para, default(pr).getOrElse("user_company", ""), group)(aggregateSalesResult(_)(uid))
			
			(Some(Map("history" -> toJson(sumSales.get)) ++ pr.get ++ data.as[JsObject].value.toMap), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResultHistoryCurVsPreWithCity(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val prTemp = default(pr)
			
			val condition = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Date" -> alDateOpt.yyyyMM2EarlyLong(prTemp("Date")), "Market" -> prTemp("Market"))
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Date" -> alDateOpt.yyyyMM2EarlyLong(tmp.head), "Market" -> tmp.tail.head)
			}
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market", "Provice" -> "$Provice", "Product" -> "$Product"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
			
			val uid = Sercurity.md5Hash(alDateOpt.Timestamp2yyyyMM(condition.getAs[Long]("Date").get) + condition.getAs[String]("Market").get)
			val history = db.aggregate(condition, prTemp("user_company"), group)(aggregateSalesResult(_)(uid))
			(Some(Map("pre_result" -> toJson(history.get)) ++ pr.get ++ data.as[JsObject].value.toMap), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResultSalesVsShare(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val para = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Date" -> DateUtil.getDateLong(default(pr).getOrElse("Date", "0")), "Market" -> default(pr).getOrElse("Market", ""))
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Date" -> DateUtil.getDateLong(tmp.head), "Market" -> tmp.tail.head)
			}
			
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market", "Product" -> "$Product"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
			val table = (data \ "condition" \ "table").asOpt[String].getOrElse("")
			val uid = Sercurity.md5Hash(alDateOpt.Timestamp2yyyyMM(para.getAs[Long]("Date").get) + para.getAs[String]("Market").get)
			
			val cur = db.aggregate(para, table, group)(aggregateSalesResult(_)(uid))
			(Some(Map("cur" -> toJson(cur.get)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResultCurVsShare(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val temp = default(pr)
			val para = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Date" -> DateUtil.getDateLong(temp("Date")), "Market" -> temp("Market"))
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Date" -> DateUtil.getDateLong(tmp.head), "Market" -> tmp.tail.head)
			}
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market", "Provice" -> "$Provice", "Product" -> "$Product"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
			val table = (data \ "condition" \ "table").asOpt[String].getOrElse("")
			val uid = Sercurity.md5Hash(alDateOpt.Timestamp2yyyyMM(para.getAs[Long]("Date").get) + para.getAs[String]("Market").get)
			val cur = db.aggregate(para, table, group)(aggregateSalesResult(_)(uid))
			(Some(Map("cur_result" -> toJson(cur.get)) ++ pr.get), None)
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
		// TODO: 重构啊，重构啊
		val selectDate = (pr.get("condition") \ "marketWithYear").asOpt[String] match {
			case None => default(pr)("Date")
			case Some(one) => one.split("-").head
		}
		val selectMarket = (pr.get("condition") \ "marketWithYear").asOpt[String] match {
			case None => default(pr)("Market")
			case Some(one) => one.split("-").tail.head
		}
		// TODO： 想一想，怎么才能知道公司名字呢
		val product = "辉瑞"
		
		val timeLst = alNearDecemberMonth.diff12Month(selectDate).toList.dropRight(1)
		
		val curTemp = mergerResult("cur").as[String Map List[String Map String]].values.toList.flatten
		val historyTemp = mergerResult("history").as[String Map List[String Map String]].values.toList.flatten
		
		val curPSumS = curTemp.filter(f => f("Product").contains(product)).map(x => x("Sales").toDouble).sum
		val curMSumS = curTemp.map(x => x("Sales").toDouble).sum
		val curMSumU = curTemp.map(x => x("Units").toDouble).sum
		val share = if(curMSumS == 0) 0 else ((curPSumS / curMSumS) * 100).formatted("%.2f").toDouble
		val result = historyTemp match {
			case Nil =>
 				timeLst.filterNot(f => f == selectDate).map ( x =>Map("Date" -> x, "Market" -> selectMarket, "Sales" -> "0", "Units" ->"0", "Share" ->"0")) :+
				Map("Date" -> selectDate, "Market" -> selectMarket, "Sales" -> curMSumS.toString, "Units" ->curMSumU.toString, "Share" -> share.toString)
			case hlst =>
				timeLst.map{ x =>
					val hisPSumS = hlst.filter(f => f("Date") == x && f("Product").contains(product)).map(z => z("Sales").toDouble).sum
					val hisMSumS = hlst.filter(f => f("Date") == x).map(z => z("Sales").toDouble).sum
					val hisMSumU = hlst.filter(f => f("Date") == x).map(z => z("Units").toDouble).sum
					val share = if(hisMSumS == 0) 0 else ((hisPSumS / hisMSumS) * 100).formatted("%.2f").toDouble
					Map("Date" -> x, "Market" -> selectMarket , "Sales" -> hisMSumS.toString, "Units" -> hisMSumU.toString, "Share" -> share.toString)
				} :+ Map("Date" -> selectDate, "Market" -> selectMarket, "Sales" -> curMSumS.toString, "Units" ->curMSumU.toString, "Share" -> share.toString)
		}

		Map("condition" -> toJson(result),
			"result_condition" -> (mergerResult("result_condition") \ "select_values").getOrElse(throw new Exception("")),
			"cursales" -> toJson(curMSumS),
			"selectDate" -> toJson(selectDate),
			"selectMarket" -> toJson(selectMarket),
			"curproductsales" -> toJson(curPSumS)
		   )
	}
	
	def salesMapWithCityResultMerge(lst: List[Map[String, JsValue]])(pr: Option[Map[String, JsValue]]): Map[String, JsValue] = {
		val para = MergeParallelResult(lst)
		// TODO: 重构啊，重构啊
		val selectDate = (pr.get("condition") \ "marketWithYear").asOpt[String] match {
			case None => default(pr)("Date")
			case Some(one) => one.split("-").head
		}
		val selectMarket = (pr.get("condition") \ "marketWithYear").asOpt[String] match {
			case None => default(pr)("Market")
			case Some(one) => one.split("-").tail.head
		}
		val product = "辉瑞"
		val cuProvinceTemp = para("cur_result").as[String Map List[String Map String]].values.flatten
		val result = cuProvinceTemp.groupBy(g => g("Province")).map { x =>
			val sumSales = x._2.map(f => f("Sales").toDouble).sum
			val sumUnits = x._2.map(f => f("Units").toDouble).sum
			val sumProductSales = x._2.filter(f => f("Product").contains(product)).map(f => f("Sales").toDouble).sum
			val share =  if(sumSales == 0) 0 else ((sumProductSales / sumSales) * 100).formatted("%.2f").toDouble
			Map("Date" -> toJson(selectDate), "Market" -> toJson(selectMarket), "Province" -> toJson(x._1), "Sales" -> toJson(sumSales), "Units" -> toJson(sumUnits), "ProductSales" -> toJson(sumProductSales), "Share" -> toJson(share) )
		}.toList.sortBy(s => s("Sales").as[Double]).reverse
		
		Map("condition" -> toJson(result), "bar" -> toJson(result.take(10).reverse))
	}
	
	def withYeaForCurVsPre(lst: List[Map[String, JsValue]])(pr: Option[Map[String, JsValue]]): Map[String, JsValue] = {
		val para = MergeParallelResult(lst)
		val curLst = para("cur_result").as[String Map JsValue].values.head.as[List[String Map JsValue]].sortBy(x => x("Sales").as[Double]).reverse.take(6)
		val preLst = para("pre_result").as[String Map JsValue].values.head.as[List[String Map JsValue]].sortBy(x => x("Sales").as[Double]).reverse
		val preOpt = curLst.map { x =>
			preLst.find(z => z("Date") == x("Date") && z("City") == x("City")) match {
				case None =>
					Map("Market" -> x("Market"), "City" -> x("City"), "Date" -> x("Date"), "Units" -> toJson(0), "Sales" -> toJson(0))
				case Some(f) => f
			}
		}
		Map("condition" -> toJson(Map("curLst" -> curLst, "preLst" -> preOpt)))
	}
	
	def conditionResultMerge(lst: List[Map[String, JsValue]])
	                        (pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): Map[String, JsValue] = {
		val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
		val db = conn.queryDBInstance("cli").get
		
		//		def merge(lst: List[String Map JsValue])(t: String Map JsValue): String Map JsValue = {
		//			lst match {
		//				case Nil => Map.empty
		//				case head :: tail =>
		//					val tmp = if(t.isEmpty) t ++ head
		//					else {
		//						head map{ x =>
		//							val v = t.get(x._1).get.as[String]
		//							(x._1, toJson((x._2.as[String] :: v :: Nil).distinct))
		//						}
		//					}
		//					tmp ++ merge(tail)(tmp)
		//			}
		//		}
		
		val para = MergeParallelResult(lst)
		val temp = para.values.map(x => x.as[List[String Map JsValue]]).toList.flatten
		val o = DBObject("user_id" -> (pr.get("user") \ "user_id").as[String])
		val company = db.queryObject(o, "users") { x =>
			val profile = x.getAs[MongoDBObject]("profile")
			val company = profile.get.getAs[String]("company")
			Map("user_company" -> toJson(company))
		} match {
			case None => throw new Exception("data not exist")
			case Some(one) => Map("user_company" -> one("user_company"))
		}
		//merge(temp)(Map.empty)
		
		Map("result_condition" -> toJson(Map("select_values" -> toJson(temp)))) ++ company
	}
	
	
	def default(pr: Option[Map[String, JsValue]]): String Map String = {
		pr match {
			case None => throw new Exception("pr data not exist")
			case Some(one) =>
				one.get("result_condition").map(x =>
					x.as[String Map JsValue].get("select_values").map(z =>
						z.as[List[String Map String]].sortBy(s => s("Date"))).
						getOrElse(throw new Exception("data not exist"))).
					getOrElse(throw new Exception("data not exist")).head ++ Map("user_company" -> one("user_company").as[String])
		}
	}
}
