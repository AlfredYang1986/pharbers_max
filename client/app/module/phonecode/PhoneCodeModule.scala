package module.phonecode

import com.mongodb.casbah.Imports._
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.sercurity.Sercurity
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.phonecode.PhoneCodeData.PhoneCodeData
import module.phonecode.PhoneCodeMessages._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object PhoneCodeModule extends ModuleTrait with PhoneCodeData {
	
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_send_sms_code(data) => sendSMSCode(data)(pr)
		case msg_check_sms_code(data) => checkSMSCode(data)(pr)
		case _ => ???
	}
	
	
	def sendSMSCode(data : JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
		implicit val db = conn.queryDBInstance("cli").get
		try {
			val token = pr.map(x => x.get("auth").get).getOrElse(throw new Exception("phone error"))
			val o : DBObject = m2d(token)
			val phone = (token \ "phone").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			val condition = DBObject("phone" -> phone)
			db.queryObject(condition, "phonecode") match {
				case None => db.insertObject(o, "phonecode", "phone")
				case Some(one) => db.updateObject(o, "phonecode", "phone")
			}
			val result = toJson(d2m(o))
			(Some(Map("reg" -> result)), None)
		} catch {
			case ex : Exception =>
				println(ex)
				(None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def checkSMSCode(data : JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
		implicit val db = conn.queryDBInstance("cli").get
		
		try {
			val token = pr.map(x => x.get("auth").get).getOrElse(throw new Exception("phone error"))
			val phoneNo = (token \ "phone").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			val code = (data \ "code").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			val reg_token = (data \ "reg_token").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			if (!Sercurity.getTimeSpanWithPast10Minutes.map (x => Sercurity.md5Hash(phoneNo + x)).contains(reg_token)) throw new Exception("token exprie")
			val condition = MongoDBObject("phone" -> phoneNo, "code" -> code)
			db.deleteObject(condition, "phonecode", "phone")
			(Some(Map("result" -> toJson("success"))), None)
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
