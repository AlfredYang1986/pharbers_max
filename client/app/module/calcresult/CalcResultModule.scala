package module.calcresult

import com.pharbers.ErrorCode
import com.pharbers.aqll.common.MergeParallelResult
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.calcresult.CalcResultData.CalcResultData
import module.calcresult.CalcResultMessage._
import play.api.libs.json.JsValue
import play.api.libs.json.Json._
import com.mongodb.casbah.Imports._

import scala.collection.immutable.Map

object CalcResultModule extends ModuleTrait with CalcResultData {
	
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgCalcResultCondition(data: JsValue) => queryCalcResultConditions(data)(pr)
		case MsgCalcResult(data: JsValue) => queryCalcResult(data)(pr)
		case _ => throw new Exception("function is not impl")
	}
	
	def queryCalcResult(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			println(pr)
			println(data)
			
			
			(Some(Map("calc_result" -> toJson("0"))), None)
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
			val reVal = db.aggregate(DBObject(), table, group)(aggregateSalesResult)
			(reVal, None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def detailResultMerge(lst : List[Map[String, JsValue]])
	                     (pr : Option[Map[String, JsValue]]) : Map[String, JsValue] = {
		
		val para = MergeParallelResult(lst)
		println(s"lst = $lst")
//		println(s"detailResultMerge para = $para")
//		println(s"detailResultMerge paprra = $pr")
		
		Map("condition" -> toJson(""))
	}
	
	def conditionResultMerge(lst : List[Map[String, JsValue]])
	                        (pr : Option[Map[String, JsValue]]) : Map[String, JsValue] = {
		
		def merge(lst: List[String Map JsValue])(t: String Map JsValue): String Map JsValue = {
			lst match {
				case Nil => Map.empty
				case head :: tail =>
					val tmp =if(t.isEmpty) t ++ head
					else {
						head map{ x =>
							val v = t.get(x._1).get.as[String]
							(x._1, toJson(x._2.as[String] :: v :: Nil))
						}
					}
					tmp ++ merge(tail)(tmp)
			}
		}
		
		val para = MergeParallelResult(lst)
		val temp = para.values.map ( x => x.as[Map[String, JsValue]]).toList
		
		Map("result_condition" -> toJson(merge(temp)(Map.empty)))
	}
	
}
