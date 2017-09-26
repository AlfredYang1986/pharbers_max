package module.phonecode

import com.mongodb.casbah.Imports._
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.sercurity.Sercurity
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.phonecode.PhoneCodeData._
import module.phonecode.PhoneCodeMessages._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object PhoneCodeModule extends ModuleTrait with PhoneCodeData {
	
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_send_sms_code(data) => sendSMSCode(data)(pr)
		case msg_check_sms_code(data) => checkSMSCode(data)(pr)
		case _ => throw new Exception("function is not impl")
	}
	
	
	def sendSMSCode(data : JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
		val db = conn.queryDBInstance("cli").get
		try {
			val token = pr match {
				case None => throw new Exception("pr data not exist")
				case Some(one) => one.get("auth").map(x => x).getOrElse(throw new Exception("token parse error"))
			}
			
			val o : DBObject = m2d(token)
			val phone = (token \ "phone").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			val condition = DBObject("phone" -> phone)
			db.queryObject(condition, "phonecode") match {
				case None => db.insertObject(o, "phonecode", "phone")
				case Some(_) => db.updateObject(o, "phonecode", "phone")
			}
			val result = toJson(d2m(o) - "code" ++ Map("flag" -> toJson("ok")))
			(Some(Map("reg" -> result)), None)
		} catch {
			case ex : Exception =>
				println(ex)
				(None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def checkSMSCode(data : JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
		val db = conn.queryDBInstance("cli").get
		
		try {
			val token = pr match {
				case None => throw new Exception("pr data not exist")
				case Some(one) => one.get("auth").map(x => x).getOrElse(throw new Exception("token parse error"))
			}
//			val token = pr.map(x => x.get("auth").get).getOrElse(throw new Exception("phone error"))
			val phoneNo = (token \ "phone").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			val code = (data \ "code").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			val reg_token = (data \ "reg_token").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			if (!Sercurity.getTimeSpanWithPast10Minutes.map (x => Sercurity.md5Hash(phoneNo + x)).contains(reg_token)) throw new Exception("token exprie")
			val condition = MongoDBObject("phone" -> phoneNo, "code" -> code)
			db.queryObject(condition, "phonecode") match {
				case None => throw new Exception("reg phone or code error")
				case Some(_) =>
//					db.deleteObject(condition, "phonecode", "phone")
					(Some(Map("result" -> toJson("success"))), None)
			}
			(Some(Map("result" -> toJson("success"))), None)
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
