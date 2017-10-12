package module.phonecode

import com.mongodb.casbah.Imports._
import com.pharbers.ErrorCode
import com.pharbers.sercuity.Sercurity
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.sercuity.Sercurity
import module.phonecode.PhoneCodeData._
import module.phonecode.PhoneCodeMessages._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object PhoneCodeModule extends ModuleTrait with PhoneCodeData {
	
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_send_sms_code(data) => sendSMSCode(data)
		case msg_check_sms_code(data) => checkSMSCode(data)
		case msg_check_send_time(data) => checkSendTime(data)
		case _ => throw new Exception("function is not impl")
	}
	
	def checkSendTime(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val o: DBObject = conditions(data)
			db.queryObject(o, "phonecode") match {
				case None => (Some(Map("reg" -> toJson("ok"))), None)
				case Some(x) =>
					val reg_token = x.get("reg_token").get.as[String]
					val phoneNo = (data \ "condition" \ "phone").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
					if (Sercurity.getTimeSpanWithPastMinutes(1).map(x => Sercurity.md5Hash(phoneNo + x)).contains(reg_token)) throw new Exception("phone code time is lt 60 seconds")
					else (Some(Map("reg" -> toJson("ok"))), None)
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	
	def sendSMSCode(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val o: DBObject = m2d(data)
			val phone = (data \ "condition" \ "phone").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val condition = DBObject("phone" -> phone)
			db.queryObject(condition, "phonecode") match {
				case None => db.insertObject(o, "phonecode", "phone")
				case Some(_) => db.updateObject(o, "phonecode", "phone")
			}
			val result = toJson(d2m(o) - "code" ++ Map("flag" -> toJson("ok")))
			(Some(Map("reg" -> result)), None)
		} catch {
			case ex: Exception =>
				println(ex)
				(None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def checkSMSCode(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val phoneNo = (data \ "condition" \ "phone").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val code = (data \ "condition" \ "code").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			val reg_token = (data \ "condition" \ "reg_token").asOpt[String].map(x => x).getOrElse(throw new Exception("wrong input"))
			if (!Sercurity.getTimeSpanWithPast10Minutes.map(x => Sercurity.md5Hash(phoneNo + x)).contains(reg_token)) throw new Exception("token exprie")
			val condition = MongoDBObject("phone" -> phoneNo, "code" -> code)
			db.queryObject(condition, "phonecode") match {
				case None => throw new Exception("reg phone or code error")
				case Some(_) =>
					//					db.deleteObject(condition, "phonecode", "phone")
					(Some(Map("result" -> toJson("success"))), None)
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
