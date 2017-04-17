package module

import com.mongodb.casbah.Imports.MongoDBObject
import com.pharbers.aqll.pattern.{CommonMessage, MessageDefines, ModuleTrait}
import com.pharbers.aqll.util.{GetProperties, HTTP}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

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

	def cleaningdata(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val result = call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/uploadfile", data)
			(Some(Map("result" -> result)), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def checkExcel(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
				call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/samplecheck", data)
				(Some(Map("result" -> toJson("Ok"))), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def runModel(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val result = call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/modelcalc", data)
			(Some(Map("result" -> result)), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def commitrundata(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val result = call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/datacommit", data)
			(Some(Map("result" -> toJson("ok"))), None)
		} catch {
			case ex: Exception => (None, Some(error_handler(ex.getMessage().toInt)))
		}
	}

	def fileExport(data: JsValue)(implicit error_handler: Int => JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val result = call(GetProperties.Akka_Http_IP + ":" + GetProperties.Akka_Http_Port + "/dataexport", data)
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
		json
	}
}
