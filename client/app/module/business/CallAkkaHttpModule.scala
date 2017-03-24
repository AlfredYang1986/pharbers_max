package module.business

import akka.util.Timeout
import com.mongodb.casbah.Imports.{$and, MongoDBObject}
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.dao.{_data_connection_basic, from}
import com.pharbers.aqll.util.{GetProperties, HTTP, MD5}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits._

/**
  * Created by qianpeng on 2017/2/13.
  */
object CallAkkaHttpModuleMessage {

	sealed class msg_CallHttp extends CommonMessage

	case class msg_CallCheckExcel(data: JsValue) extends msg_CallHttp

	case class msg_CallRunModel(data: JsValue) extends msg_CallHttp

	case class msg_CallFileExport(data: JsValue) extends msg_CallHttp

	case class msg_CallCommitRunData(data: JsValue) extends msg_CallHttp

	case class msg_CallCleaningData(data: JsValue) extends msg_CallHttp

}

object CallAkkaHttpModule extends ModuleTrait {

	import CallAkkaHttpModuleMessage._
	import controllers.common.default_error_handler.f

	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_CallCheckExcel(data) => checkExcel(data)
		case msg_CallRunModel(data) => runModel(data)
		case msg_CallFileExport(data) => fileExport(data)
		case msg_CallCommitRunData(data) => commitrundata(data)
		case msg_CallCleaningData(data) => cleaningdata(data)
		case _ => println("Error---------------"); ???
	}

	def checkExcel(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			implicit val timeout = Timeout(3 minute)

			val company = (data \ "company").asOpt[String].getOrElse("")
			val filetype = (data \ "filetype").asOpt[String].getOrElse("")
			val filename = (data \ "filename").asOpt[String].getOrElse("")
			val conditions = List("Company" $eq company, "Datasource_Type" $eq "Manage")

			val d = (from db() in "DataSources" where $and(conditions)).select(managedp(_)(filename))(_data_connection_basic).toList
			d.size match {
				case 0 => (Some(Map("FinalResult" -> toJson("no"))), None)
				case _ =>
					val result = call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/checkExcel", d.head)
					(Some(Map("result" -> result)), None)
			}
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def runModel(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {

		try {
//			val company = (data \ "company").asOpt[String].getOrElse("")
//			val filetype = (data \ "filetype").asOpt[String].getOrElse("")
//			val filename = (data \ "filename").asOpt[String].getOrElse("")
//			val conditions = List("Company" $eq company, "Datasource_Type" $eq "Manage")
//			val d = (from db() in "DataSources" where $and(conditions)).select(managedp(_)(filetype, filename))(_data_connection_basic).toList
//			d.size match {
//				case 0 => (Some(Map("FinalResult" -> toJson("is null"))), None)
//				case _ =>
//					println(s"d.head = ${d.head}")
//					call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/calc", d.head)
//			}
			call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/calc", data)
			(Some(Map("FinalResult" -> toJson("ok"))), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def fileExport(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			var datatype = (data \ "datatype").asOpt[String].map (x => x).getOrElse("")
			var market = (data \ "market").asOpt[List[String]].map (x => x).getOrElse(List())
			var staend = (data \ "staend").asOpt[List[String]].map (x => x).getOrElse(List())
			var company = (data \ "company").asOpt[String].map (x => x).getOrElse("")
			var filetype = (data \ "filetype").asOpt[String].map (x => x).getOrElse("")

			var marketmap,staendmap = ""
			if(market.size != 0){
				market.foreach(x => marketmap += (x + "#"))
			}
			if(staend.size != 0){
				staend.foreach(x => staendmap += (x + "#"))
			}
			println(s"datatype=${datatype} market=${marketmap} staend=${staendmap} company=${company} filetype=${filetype}")
			var datajson  = toJson(Map("datatype" -> datatype, "marketmap" -> marketmap, "staendmap" -> staendmap, "company" -> company, "filetype" -> filetype))
			call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/export", datajson)
			(Some(Map("FinalResult" -> toJson("ok"))), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def commitrundata(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			var company = (data \ "company").asOpt[String].map (x => x).getOrElse("")
			var datajson  = toJson(Map("company" -> company))
			println(s"datajson=${datajson}")
			call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/commit", datajson)
			(Some(Map("result" -> toJson("ok"))), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def cleaningdata(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			var company = (data \ "company").asOpt[String].map (x => x).getOrElse("")
			var datajson  = toJson(Map("company" -> company))
			println(s"datajson=${datajson}")
			val result = call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/cleandata", datajson)
			println(s"result=${result}")
			(Some(Map("result" -> result)), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def managedp(d: MongoDBObject)(filename: String): JsValue = {
		val company = d.getAs[String]("Company").get
		val hospmatchpath = d.getAs[String]("File_Path").get
		toJson(Map("filename" -> filename,
				   "company" -> company,
			       "hospmatchpath" -> hospmatchpath
				))
	}

	def call(uri: String, data: JsValue): JsValue = {
		val json = (HTTP(uri)).post(data).as[JsValue]
		println(s"json=${json}")
		json
	}
}
