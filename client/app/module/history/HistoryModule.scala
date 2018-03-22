package module.history

import com.mongodb.{BasicDBObject, QueryOperators}
import com.mongodb.casbah.Imports._
import com.pharbers.ErrorCode
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.history.HistoryData.HistoryData
import module.history.HistoryMessage._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map

object HistoryModule extends ModuleTrait with HistoryData {
	
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgQueryHistorySelect(data) => queryHistorySelect(data)(pr)
		case MsgQueryHistoryCount(data) => queryMultipleObjectCount(data)(pr)
		case MsgQueryHistoryByPage(data) => queryMultipleObject(data)(pr)
		case _ => throw new Exception("function is not impl")
	}
	
	def queryHistorySelect(data: JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val result = pr match {
				case None => throw new Exception("")
				case Some(x) =>
					val group = DBObject("_id" -> DBObject("Market" -> "$Market"))
					db.aggregate(DBObject(), x("user").as[String Map JsValue].get("company").get.as[String], group)(aggregateHistorySelectResult)
			}
			(Some(Map("condition" -> toJson(result))), None)
		} catch {
			case ex: Exception => println(s"ErrorCode=${ErrorCode}"); (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryMultipleObjectCount(data: JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
            val db = conn.queryDBConnection("calc").get
			var count: Int = 0
			val lst = pr match {
				case None => throw new Exception("")
				case Some(x) => {
                    db.getCollection(s"${x("user").as[String Map JsValue].get("company").get.as[String]}_dictionary").find(DBObject("ym" -> new BasicDBObject(QueryOperators.EXISTS, true)))
				}
			}
			val lstColl = lst.toList.map(x => x.get("collectionName").asInstanceOf[String])
			lstColl.foreach(x => count += db.getCollection(x).count())
			(Some(Map("count" -> toJson(count), "lstColl" -> toJson(lstColl)) ++ pr.get), None)
		} catch {
			case ex: Exception => println(s"*************${ex.getMessage}"); (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryMultipleObject(data: JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("calc").get
			val o = conditions(data)
			val skip = (data \ "condition" \ "skip").asOpt[Int].map(x => x).getOrElse(0)
			val take = (data \ "condition" \ "take").asOpt[Int].map(x => x).getOrElse(10)
			implicit val result: List[String Map JsValue] = pr match {
				case None => throw new Exception("")
				case Some(x) => {
					var r: List[String Map JsValue] = List.empty
					x("lstColl").as[List[String]].foreach(coll => r = r ::: db.queryMultipleObject(o, coll, "Date", skip = skip, take = take))
					r
				}
			}
			val html = tableOutHtml(data)
			(Some(Map("condition" -> toJson(html.toString), "count" -> toJson(pr.get("count").as[Int]), "skip" -> toJson(skip), "take" -> toJson(take))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
}
