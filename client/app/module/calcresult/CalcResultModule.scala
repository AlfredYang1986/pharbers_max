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
		case MsgCalcResultCondition(data: JsValue) => queryCalcResultConditions(data)(pr)
		case MsgCalcResult(data: JsValue) => queryCalcResult(data)(pr)
		case _ => throw new Exception("function is not impl")
	}
	
	def queryCalcResultHistorySumSales(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val group = DBObject("_id" -> DBObject("Market" -> "$Market"), "Sales" -> DBObject("$sum" -> "$f_sales"), "Units" -> DBObject("$sum" -> "$f_units"))
			
			val para = (data \ "condition" \ "marketWithYear").asOpt[String] match {
				case None => DBObject("Market" -> default(data)(pr).get("Market").get)
				case Some(x) =>
					val tmp = x.split("-")
					DBObject("Market" -> tmp.tail.head)
			}
			val uid = Sercurity.md5Hash(default(data)(pr).get("Date").get + para.getAs[String]("Market").get)
			val sumSales = db.aggregate(para, default(data)(pr).get("user_company").get, group)(aggregateSalesResult(_)(uid))
			(Some(Map("allSalesSum" -> toJson(sumSales.get)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryCalcResult(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
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
			(Some(Map("cur" -> toJson(cur.get), "allSalesSum" -> pr.get("allSalesSum"), "history" -> toJson(history)) ++ pr.get("result_condition").as[String Map JsValue]), None)
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
	
	def detailResultMerge(lst: List[Map[String, JsValue]])
	                     (pr: Option[Map[String, JsValue]]): Map[String, JsValue] = {
		
		val para = MergeParallelResult(lst)
		val select = para("select_values")
		val salesVsShare = para - "select_values"
		
		
		val allSalesSum = salesVsShare.get("allSalesSum").map { x => x.as[String Map JsValue].head._2.as[List[String Map JsValue]] }.getOrElse(Nil)
		val curtmp = salesVsShare("cur").as[String Map JsValue]
		val histmp = salesVsShare("history").as[String Map JsValue]
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
							if (sum == 0) {
								z ++ Map("Share" -> toJson(0)) :: Nil
							} else {
								(timeLst.filterNot(y => y == z("Date").as[String]).map { o =>
									z ++ Map("Date" -> toJson(o), "Units" -> toJson(0), "Sales" -> toJson(0), "Market" -> toJson(z("Market").as[String]), "Share" -> toJson(0))
								} :+ z ++ Map("Share" -> toJson(z("Sales").as[Double] / sum * 100))).sortBy(s => s("Date").as[String]).filterNot(f => f("Date").as[String] == curtmp.flatMap(x => x._2.as[List[String Map JsValue]].map(z => z("Date").as[String])).head)
							}
						}
				}
			)
		}))
		val chart = Map("select_values" -> select, "sales_vs_share" -> toJson(salesVsShare - "allSalesSum" ++ cur ++ history))
		
		Map("condition" -> toJson(chart))
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
		val o = DBObject("user_id" -> (pr.get.get("user").get \ "user_id").as[String])
		val company = db.queryObject(o, "users") { x =>
			val profile = x.getAs[MongoDBObject]("profile")
			val company = profile.get.getAs[String]("company")
			Map("user_company" -> toJson(company))
		} match {
			case None => throw new Exception("data not exist")
			case Some(one) => Map("user_company" -> one.get("user_company").get)
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
						z.as[List[String Map String]]).
						getOrElse(throw new Exception("data not exist"))).
					getOrElse(throw new Exception("data not exist")).head ++ Map("user_company" -> one.get("user_company").get.as[String])
		}
	}
}
