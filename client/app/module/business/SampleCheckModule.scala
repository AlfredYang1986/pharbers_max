package module.business

import java.util.Date

import play.api.libs.json._
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.CommonMessage
import com.pharbers.aqll.pattern.ModuleTrait
import com.pharbers.aqll.pattern.MessageDefines
import com.pharbers.aqll.util.dao.from
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.util.dao._data_connection_cores
import com.pharbers.aqll.util.{DateUtil, GetProperties, MD5}
import com.sun.xml.internal.bind.v2.TODO

// TODO 为了赶时间，暂时先这样写了，后续第一时间做处理 自己都看不下去了(^ ^)

object SampleCheckModuleMessage {
	sealed class msg_CheckBaseQuery extends CommonMessage
	case class msg_samplecheck(data: JsValue) extends msg_CheckBaseQuery
	case class msg_samplecheckyesteryear(data: JsValue) extends msg_CheckBaseQuery
	case class msg_samplecheckyestermonth(data: JsValue) extends msg_CheckBaseQuery

	case class msg_samplechecktopline(data: JsValue) extends msg_CheckBaseQuery
	case class msg_samplecheckplot(data: JsValue) extends msg_CheckBaseQuery
}

object SampleCheckModule extends ModuleTrait {

	import SampleCheckModuleMessage._
	import controllers.common.default_error_handler.f

	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_samplecheck(data) => msg_check_func(data)
		case msg_samplecheckyesteryear(data) => msg_check_yesteryear_func(data)(pr)
		case msg_samplecheckyestermonth(data) => msg_check_yestermonth_func(data)(pr)
		case msg_samplechecktopline(data) => msg_check_topcline_func(data)(pr)
		case msg_samplecheckplot(data) => msg_check_chartplot_func(data)(pr)
		case _ => println("Error---------------"); ???
	}

	def msg_check_func(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val company = (data \ "company").asOpt[String].getOrElse("")
		val filename = (data \ "filename").asOpt[String].getOrElse("")
		//"123456"
		val id = MD5.md5(GetProperties.loadConf("File.conf").getString("Files.UpClient_File_Path")+filename+company+DateUtil.getIntegralStartTime(new Date()).getTime.toString)
		try {
			val conditions = ("ID" -> id)
			val d = (from db() in "FactResult" where $and(conditions)).select(resultCheckFuncData(_))(_data_connection_cores).toList
			d.size match {
				case 0 =>
					(Some(Map("CurResult" -> toJson(Map("flag" -> toJson("not data"))))), None)
				case _ => (Some(Map("CurResult" -> d.head)), None)
			}
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def msg_check_yesteryear_func(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		//"098f6bcd4621d373cade4e832627b4f6"
		val company = (data \ "company").asOpt[String].getOrElse("")
		val prJson: JsValue = pr match {
			case Some(x) => x.get("CurResult").get
			case _ => ???
		}
		val time = (prJson \ "date").as[Long]
		val year = (DateUtil.getDateLongForString(time).split("-")(0).toInt - 1).toString
		val month = DateUtil.getDateLongForString(time).split("-")(1)
		val timelong = DateUtil.getDateLong(year + month)
		try {
			val conditions = List("Company" $eq company, "Date" $eq timelong)
			val d = (from db() in "SampleCheckResult" where $and(conditions)).select(resultYesterYearTimeFuncData(_)(pr))(_data_connection_cores).toList
			d.size match {
				case 0 =>
					val flag = Map("YesterYear" -> toJson("not data"))
					(Some(Map("YesterYearResult" -> toJson(flag ++ pr.get))), None)
				case _ => (Some(Map("YesterYearResult" -> d.head)), None)
			}
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def msg_check_yestermonth_func(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit error_handel: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		//"098f6bcd4621d373cade4e832627b4f6"
		val company = (data \ "company").asOpt[String].getOrElse("")
		val prJson: JsValue = pr match {
			case Some(x) => x.get("YesterYearResult").get.as[Map[String, JsValue]].get("CurResult").get
			case _ => ???
		}
		val time = (prJson \ "date").as[Long]
		val year = DateUtil.getDateLongForString(time).split("-")(0)
		val month = (DateUtil.getDateLongForString(time).split("-")(1).toInt - 1).toString
		val timelong = DateUtil.getDateLong(year + month)
		val conditions = List("Company" $eq company, "Date" $eq timelong)
		val d = (from db() in "SampleCheckResult" where $and(conditions)).select(resultAgoMonthTimeFuncData(_)(pr))(_data_connection_cores).toList
		d.size match {
			case 0 =>
				val flag = Map("AgoMonth" -> toJson("not data"))
				(Some(Map("FinalResult" -> toJson(flag ++ pr.get.get("YesterYearResult").get.as[Map[String, JsValue]]))), None)
			case _ => (Some(Map("FinalResult" -> d.head)), None)
		}
	}

	def msg_check_topcline_func(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		//"098f6bcd4621d373cade4e832627b4f6"
		val company = (data \ "company").asOpt[String].getOrElse("")
		val prJson: JsValue = pr match {
			case Some(x) => x.get("CurResult").get
			case _ => ???
		}
		val time = (prJson \ "date").as[Long]
		val agotime = DateUtil.getAgoMonthTime(time, 12)
//		println(s"time = $time , ${DateUtil.getDateLongForString(time)}")
//		println(s"agotime = $agotime , ${DateUtil.getDateLongForString(agotime)}")
		try {
			val conditions = List("Company" $eq company, "Date" $gte agotime $lte time)
			val d = (from db() in "SampleCheckResult" where $and(conditions)).select(resulrTopChartsFuncData(_))(_data_connection_cores).toList.sortBy{x => (x \ "Date").as[Long]}
			d.size match {
				case 0 => (Some(Map("TopChartResult" -> toJson(Map("flag" -> toJson("not data"))))), None)
				case _ => (Some(Map("TopChartResult" -> toJson(d))), None)
			}
		}catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage.toInt)))
		}
	}

	def msg_check_chartplot_func(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		//"098f6bcd4621d373cade4e832627b4f6"
		val company = (data \ "company").asOpt[String].getOrElse("")
		val prJson: JsValue = pr match {
			case Some(x) => x.get("CurResult").get
			case _ => ???
		}
		val time = new Date().getTime
		val yester = DateUtil.getDateByYesterYear(time)
		val conditions = List("Company" $eq company, "Date" $gte yester $lte time)
		val d = (from db() in "SampleCheckResult" where $and(conditions)).select(resultChartsPlot(_))(_data_connection_cores).toList
		d.size match {
			case 0 => (Some(Map("PlotChartsResult" -> toJson(Map("flag" -> toJson("not data"))))), None)
			case _ => (Some(Map("TopChartResult" -> toJson(d))), None)
		}
	}

	def resultCheckFuncData(d: MongoDBObject): JsValue = {
		val t = d.getAs[MongoDBObject]("Condition").get
		val hospNum = d.getAs[Number]("HospitalNum").get.longValue
		val miniProNum = d.getAs[Number]("ProductMinuntNum").get.intValue
		val marketNum = d.getAs[Number]("MarketNum").get.intValue
		val sales = d.getAs[Number]("Sales").get.doubleValue
		val hospList = t.getAs[MongoDBList]("Hospital").get.toList.asInstanceOf[List[String]]
		val date = d.getAs[Number]("Date").get.longValue()
		//val miniPorList = t.getAs[MongoDBList]("ProductMinunt").toList.asInstanceOf[List[String]]
		toJson(Map("hospNum" -> toJson(hospNum),
			"miniProNum" -> toJson(miniProNum),
			"marketNum" -> toJson(marketNum),
			"sales" -> toJson(sales),
			"hospList" -> toJson(hospList),
			"date" -> toJson(date)
		))
	}

	def resultAgoMonthTimeFuncData(d: MongoDBObject)(pr: Option[Map[String, JsValue]]): JsValue = {
		val agoHospNum = d.getAs[Number]("HospNum").get.longValue()
		val agoMiniProNum = d.getAs[Number]("ProductNum").get.longValue()
		val agoMarketNum = d.getAs[Number]("MarketNum").get.longValue()
		val prmap = pr match {case Some(x) => x case _ => null}
		val map: Map[String, JsValue] = Map("agoHospNum" -> toJson(agoHospNum),
			"agoMiniProNum" -> toJson(agoMiniProNum),
			"agoMarketNum" -> toJson(agoMarketNum))
		toJson(Map("AgoMonth" -> toJson(map)) ++ prmap.get("YesterYearResult").get.as[Map[String, JsValue]])
	}

	def resultYesterYearTimeFuncData(d: MongoDBObject)(pr: Option[Map[String, JsValue]]): JsValue = {
		val yesterYearHospNum = d.getAs[Number]("HospNum").get.longValue()
		val yesterYearMiniProNum = d.getAs[Number]("ProductNum").get.longValue()
		val yesterYearMarketNum = d.getAs[Number]("MarketNum").get.longValue()
		val prmap = pr match {case Some(x) => x case _ => null}
		val map: Map[String, JsValue] = Map("yesterYearHospNum" -> toJson(yesterYearHospNum),
			"yesterYearMiniProNum" -> toJson(yesterYearMiniProNum),
			"yesterYearMarketNum" -> toJson(yesterYearMarketNum))
		toJson(Map("YesterYear" -> toJson(map)) ++ prmap)
	}

	def resulrTopChartsFuncData(d: MongoDBObject): JsValue = {
		val hospNum = d.getAs[Number]("HospNum").get.longValue()
		val miniProNum = d.getAs[Number]("ProductNum").get.longValue()
		val marketNum = d.getAs[Number]("MarketNum").get.longValue()
		val date = d.getAs[Number]("Date").get.longValue()
		toJson(Map("hospNum" -> toJson(hospNum), "miniProNum" -> toJson(miniProNum), "marketNum" -> toJson(marketNum), "Date" -> toJson(date)))
	}

	def resultChartsPlot(d: MongoDBObject): JsValue = {
		val time = DateUtil.getDateLongForString(d.getAs[Number]("Date").get.longValue())
		val sales = d.getAs[Number]("Sales").get.longValue()
		toJson(Map("time" -> toJson(time), "sales" -> toJson(sales)))
	}
}