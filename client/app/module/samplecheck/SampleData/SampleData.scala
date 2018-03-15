package module.samplecheck.SampleData

import com.mongodb.casbah.Imports._
import com.pharbers.driver.redis.phRedisDriver
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt
import com.pharbers.panel.phPanelFilePath
import com.pharbers.panel.util.excel.{phExcelFileInfo, phHandleExcel}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map

trait SampleData extends phPanelFilePath {
	
	class CalcType(n: Int, s: String)
	case class CalcSum() extends CalcType(0, "sum")
	case class CalcSize() extends CalcType(1, "size")
	
	def csv2SampleCheckData(pathLst: List[String], company: String): List[String Map String] = {
		pathLst.flatMap{ p =>
			alFileOpt(s"$client_path$company$output_path$p").requestDataFromFile(x => x).map { x =>
				val s =  x.toString.split(31.toChar)
				Map("phaId" -> s(5), "hospitalName" -> s(1), "date" -> s(2), "productMini" -> s(3), "market" -> s(7), "units" -> s(9), "sales" -> s(10))
			}
		}
	}
	
	def xlsx2SampleCheckData(path: String, company: String): List[String Map String] = {
		phHandleExcel().read2Lst(phExcelFileInfo(s"$client_path$company/Manage/matchFile/${universe_file.replace("##market##", path)}", 1))
	}
	
	def queryPanelWithRedis(uid: String): List[String] = {
		val rid = phRedisDriver().commonDriver.hget(uid, "rid").map(x=>x).getOrElse(throw new Exception("not found rid"))
		val panelLst = phRedisDriver().commonDriver.smembers(rid).map(x=>x.map(_.get)).getOrElse(throw new Exception("rid list is none"))
		panelLst.toList
	}
	
	def condition(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "market").asOpt[String].map(x => builder += "Market" -> x).getOrElse(throw new Exception("wrong input"))
		builder.result
	}
	
	implicit val d2m: DBObject => String Map JsValue = { o =>
		Map("Market" -> toJson(o.getAs[String]("Market").map(x => x).getOrElse(throw new Exception("data not exist"))),
			"Month" -> toJson(o.getAs[String]("Month").map(_.toInt).getOrElse(throw new Exception("data not exist"))),
			"Sales" -> toJson(o.getAs[Number]("Sales").map(_.doubleValue().formatted("%.2f")).getOrElse(throw new Exception("data not exist"))),
			"Hospital" -> toJson(o.getAs[Number]("HOSP_ID").map(_.intValue()).getOrElse(throw new Exception("data not exist"))),
			"Product" -> toJson(o.getAs[Number]("Prod_Name").map(_.intValue()).getOrElse(throw new Exception("data not exist"))))
	}
	
	def createEchartsData(data: JsValue, key: String, t: CalcType = CalcSize()): String Map JsValue = {
		val uid = (data \ "condition" \ "uid").asOpt[String].getOrElse(throw new Exception("wrong input"))
		val company = (data \ "condition" \ "company").asOpt[String].getOrElse(throw new Exception("wrong input"))
		val market = (data \ "condition" \ "market").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
		val date = (data \ "condition" \ "date").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
		val reVal =	csv2SampleCheckData(queryPanelWithRedis(uid), company).toStream.filter(f => f("market") == market && f("date") == date)//.map(f => f(key))
		val value = t match {
			case CalcSum() => reVal.map(f => f(key).toDouble).sum.formatted("%.2f").toDouble
			case CalcSize() => reVal.map(f => f(key)).distinct.size
			case _ => throw new Exception("wrong input")
		}
		(1 to 12).map((_, 0)).toMap.updated(date.substring(4).toInt, value).toList.sortBy(s => s._1).map(x => (x._1.toString, toJson(x._2.toString))).toMap
	}
}