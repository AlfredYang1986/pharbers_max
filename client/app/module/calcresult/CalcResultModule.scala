package module.calcresult

import com.pharbers.ErrorCode
import com.pharbers.aqll.common.MergeParallelResult
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.calcresult.CalcResultData.{CalcResultData, alNearDecemberMonth}
import module.calcresult.CalcResultMessage._
import play.api.libs.json.JsValue
import play.api.libs.json.Json._
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alDate.java.DateUtil
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import com.pharbers.sercuity.Sercurity

import scala.collection.immutable.Map

object CalcResultModule extends ModuleTrait with CalcResultData {
	
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgCalcResultHistorySumSales(data: JsValue) => queryCalcResultHistorySumSales(data)(pr)
		case MsgCalcResultHistoryCurVsPreWithCity(data: JsValue) => queryCalcResultHistoryCurVsPreWithCity(data)(pr)
		case MsgCalcResultHistoryWithYearForCurVsPre(data: JsValue) => qyeryCalcResultHistoryWithYearForCurVsPre(data)(pr)
		
		case MsgCalcResultCondition(data: JsValue) => queryCalcResultConditions(data)(pr)
		
		case MsgCalcResultCurVsPreWithCity(data: JsValue) => queryCalcResultCurVsShare(data)(pr)
		case MsgCalcResultSalesVsShare(data: JsValue) => queryCalcResultSalesVsShare(data)(pr)
		case MsgCalcResultWithYearForCurVsPre(data: JsValue) => queryCalcResultWithYearForCurVsPre(data)(pr)
		case _ => throw new Exception("function is not impl")
	}
	
	def queryCalcResultHistorySumSales(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val group = DBObject("_id" -> DBObject("Market" -> "$Market"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
			
			val para = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Market" -> default(data)(pr).getOrElse("Market", ""), "Product" -> DBObject("$regex" -> ".*辉瑞.*") )
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Market" -> tmp.tail.head, "Product" -> DBObject("$regex" -> ".*辉瑞.*") )
			}
			val uid = Sercurity.md5Hash(default(data)(pr).getOrElse("Date", "") + para.getAs[String]("Market").get)
			val sumSales = db.aggregate(para, default(data)(pr).getOrElse("user_company", ""), group)(aggregateSalesResult(_)(uid))
			(Some(Map("allSalesSum" -> toJson(sumSales.get)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResultHistoryCurVsPreWithCity(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val prtmp = default(data)(pr)
			
			val condition = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Date" -> alDateOpt.yyyyMM2EarlyLong(prtmp("Date")), "Market" -> prtmp("Market"))
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Date" -> alDateOpt.yyyyMM2EarlyLong(tmp.head), "Market" -> tmp.tail.head)
			}
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market", "City" -> "$City"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
			
			val uid = Sercurity.md5Hash(alDateOpt.Timestamp2yyyyMM(condition.getAs[Long]("Date").get) + condition.getAs[String]("Market").get)
			val history = db.aggregate(condition, prtmp("user_company"), group)(aggregateSalesResult(_)(uid))
			(Some(Map("pre_result" -> toJson(history.get)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def qyeryCalcResultHistoryWithYearForCurVsPre(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val prtmp = default(data)(pr)
			
			val condition = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Date" -> alDateOpt.yyyyMM2LastLong(prtmp("Date")), "Market" -> prtmp("Market"))
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Date" -> alDateOpt.yyyyMM2LastLong(tmp.head), "Market" -> tmp.tail.head)
			}
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market", "City" -> "$City"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))

			val uid = Sercurity.md5Hash(alDateOpt.Timestamp2yyyyMM(condition.getAs[Long]("Date").get) + condition.getAs[String]("Market").get)
			val history = db.aggregate(condition, prtmp("user_company"), group)(aggregateSalesResult(_)(uid))
			
			(Some(Map("pre_result" -> toJson(history.get)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResultSalesVsShare(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val para = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Date" -> DateUtil.getDateLong(default(data)(pr).get("Date").get), "Market" -> default(data)(pr).get("Market").get)
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Date" -> DateUtil.getDateLong(tmp.head), "Market" -> tmp.tail.head)
			}
			
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
			val table = (data \ "condition" \ "table").asOpt[String].getOrElse("")
			val uid = Sercurity.md5Hash(alDateOpt.Timestamp2yyyyMM(para.getAs[Long]("Date").get) + para.getAs[String]("Market").get)
			val history = db.aggregate(para - "Date", default(data)(pr).get("user_company").get, group)(aggregateSalesResult(_)(uid))
			val cur = db.aggregate(para, table, group)(aggregateSalesResult(_)(uid))
			(Some(Map("cur" -> toJson(cur.get), "allSalesSum" -> pr.get("allSalesSum"), "history" -> toJson(history)) ++ pr.get), None)//("result_condition").as[String Map JsValue]
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResultCurVsShare(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val temp = default(data)(pr)
			val para = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Date" -> DateUtil.getDateLong(temp("Date")), "Market" -> temp("Market"))
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Date" -> DateUtil.getDateLong(tmp.head), "Market" -> tmp.tail.head)
			}
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market", "City" -> "$City"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
			val table = (data \ "condition" \ "table").asOpt[String].getOrElse("")
			val uid = Sercurity.md5Hash(alDateOpt.Timestamp2yyyyMM(para.getAs[Long]("Date").get) + para.getAs[String]("Market").get)
			val cur = db.aggregate(para, table, group)(aggregateSalesResult(_)(uid))
			(Some(Map("cur_result" -> toJson(cur.get)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResultWithYearForCurVsPre(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val temp = default(data)(pr)
			val para = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Date" -> DateUtil.getDateLong(temp("Date")), "Market" -> temp("Market"))
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Date" -> DateUtil.getDateLong(tmp.head), "Market" -> tmp.tail.head)
			}
			val group = DBObject("_id" -> DBObject("Date" -> "$Date", "Market" -> "$Market", "City" -> "$City"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
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
		
		val para = MergeParallelResult(lst)
		val select = para("result_condition").as[String Map JsValue].get("select_values").get
		val userCompany = para("user_company")
		
		val allSalesSum = para.get("allSalesSum").map { x => x.as[String Map JsValue].head._2.as[List[String Map JsValue]] }.getOrElse(Nil)
		val curtmp = para("cur").as[String Map JsValue]
		val histmp = para("history").as[String Map JsValue]
		val sum = if (allSalesSum.isEmpty) 0
		else allSalesSum.head("Sales").as[Double]
		
		val cur = Map("cur" -> toJson(curtmp.map { x =>
			x._1 -> toJson(x._2.as[List[String Map JsValue]].map { z =>
				if (sum == 0) z ++ Map("Share" -> toJson(0))
				else z ++ Map("Share" -> toJson(z.get("Sales").get.as[Double] / sum * 100))
			})
		}))

		val timeLst = alNearDecemberMonth.diff12Month(curtmp.flatMap(x => x._2.as[List[String Map JsValue]].map(z => z("Date").as[String])).head).toList
		val history = Map("history" -> toJson(histmp.map { x =>
			x._1 -> toJson(
				x._2.as[List[String Map JsValue]] match {
					case Nil =>
						val tmp = curtmp.map(m => m._2.as[List[String Map JsValue]].head).head
						timeLst.map { x =>
							Map("Date" -> toJson(x), "Units" -> toJson(0), "Sales" -> toJson(0), "Market" -> toJson(""), "Share" -> toJson(0))
						}.sortBy(s => s("Date").as[String]).filterNot(f => f("Date").as[String] == tmp("Date").as[String])
					case _ =>
						x._2.as[List[String Map JsValue]].flatMap { z =>
							val temp = timeLst.filter(y => y == z("Date").as[String]) map { o =>
								Map("Date" -> toJson(o), "Units" -> z("Units"), "Sales" -> z("Sales"), "Market" -> z("Market"), "Share" -> toJson(z("Sales").as[Double] / sum * 100))
							}match {
								case Nil => timeLst.map(x => Map("Date" -> toJson(x), "Units" -> toJson(0), "Sales" -> toJson(0), "Market" -> toJson(z("Market").as[String]), "Share" -> toJson(0)))
								case head :: Nil => timeLst.map(x => Map("Date" -> toJson(x), "Units" -> toJson(0), "Sales" -> toJson(0), "Market" -> toJson(z("Market").as[String]), "Share" -> toJson(0))) :+ head
							}
							temp.distinct
						}.distinct.sortBy(s => s("Date").as[String]).filterNot(f => f("Date").as[String] == curtmp.flatMap(x => x._2.as[List[String Map JsValue]].map(z => z("Date").as[String])).head)
				}
			)
		}))

		val chart = Map("select_values" -> select, "user_company" -> userCompany, "sales_vs_share" -> toJson(para - "allSalesSum" ++ cur ++ history))
		
		Map("condition" -> toJson(chart))
	}
	
	def curVsPreWithCity(lst: List[Map[String, JsValue]])(pr: Option[Map[String, JsValue]]): Map[String, JsValue] = {
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
		val temp = para.values.map(x => x.as[Map[String, JsValue]]).toList
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
	
	
	def default(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): String Map String = {
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
