package module.auth

import java.util.Date

import com.mongodb.casbah.Imports._
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.{MergeStepResult, alValidationToken}
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import module.auth.AuthData._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.auth.AuthMessage.{msg_auth_token_type, _}
import com.pharbers.message.send.SendMessageTrait
import com.pharbers.sercuity.Sercurity

import scala.collection.immutable.Map


object AuthModule extends ModuleTrait with AuthData {
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_user_auth(data) => authWithPassword(data)
		case msg_auth_token_parser(data) => authTokenParser(data)
		case msg_auth_token_expire(data) => checkAuthTokenExpire(data)(pr)
		case msg_auth_create_token(data) => authCreateToken(data)(pr)
		case msg_auth_code_push_success(data) => authCodePushSuccess(data)
		case msg_auth_token_type(data) => authTokenType(data)(pr)
		case msg_auth_token_used(data) => checkAuthTokenUsed(data)
		case msg_auth_check_token_action(data) => authCheckTokenAction(data)
		case _ => throw new Exception("function is not impl")
	}
	
	def authWithPassword(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val att = cm.modules.get.get("att").map(x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
			val map = m2d(data)
			val date = new Date().getTime
			db.queryObject(map, "users") match {
				case None => throw new Exception("data not exist")
				case Some(one) =>
					val o = one - "email" - "phone" - "name"
					val uuid = Sercurity.md5Hash(one.get("email").get.as[String])
					val reVal = o + ("expire_in" -> toJson(date + 60 * 60 * 1000 * 24))
					val auth_token = att.encrypt2Token(toJson(reVal))
					(Some(Map("auth_token" -> toJson(auth_token), "uuid" -> toJson(uuid))), None)
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def checkAuthTokenExpire(data: JsValue)
	                        (pr: Option[Map[String, JsValue]])
	                        (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val auth = (MergeStepResult(data, pr) \ "auth").asOpt[JsValue].map(x => x).getOrElse(throw new Exception("token parse error"))
			
			val expire_in = (auth \ "expire_in").asOpt[Long].map(x => x).getOrElse(throw new Exception("token parse error"))
			if (new Date().getTime > expire_in) throw new Exception("token expired")
			else (pr, None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def authTokenParser(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val att = cm.modules.get.get("att").map(x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
			val auth_token = (data \ "condition" \ "user_token").asOpt[String].map(x => x).getOrElse(throw new Exception("input error"))
			val auth = att.decrypt2JsValue(auth_token)
			(Some(Map("auth" -> auth)), None)
		} catch {
			case ex: Exception =>
				(None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def authCreateToken(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val att = cm.modules.get.get("att").map(x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			implicit val db = conn.queryDBInstance("cli").get
			implicit val msg = cm.modules.get.get("msg").map(x => x.asInstanceOf[SendMessageTrait]).getOrElse(throw new Exception("no message impl"))
			
			val o = MergeStepResult(data, pr).asOpt[JsValue].map(x => jv2m(toJson(Map("condition" -> x)))).getOrElse(jv2m(data))
			val reVal = att.encrypt2Token(toJson(o + ("expire_in" -> toJson(new Date().getTime + 60 * 60 * 1000))))
			val email = o.get("email").map(x => x.as[String]).getOrElse("")
			emailAuthCode(email, reVal)
			(Some(Map("apply" -> toJson(o - "scope" - "phone"))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def authCodePushSuccess(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val att = cm.modules.get.get("att").map(x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			implicit val db = conn.queryDBInstance("cli").get
			implicit val msg = cm.modules.get.get("msg").map(x => x.asInstanceOf[SendMessageTrait]).getOrElse(throw new Exception("no message impl"))
			
			val token = (data \ "condition" \ "user_token").asOpt[String].map(x => x).getOrElse("")
			val js = att.decrypt2JsValue(token)
			val email = (js \ "email").asOpt[String].map(x => x).getOrElse(throw new Exception("data not exit"))
			val name = (js \ "name").asOpt[String].map(x => x).getOrElse(throw new Exception("data not exit"))
			//TODO 还未知该URL参数是否有用，暂时不删除
			val reVal = att.encrypt2Token(toJson(js.as[Map[String, JsValue]] + ("expire_in" -> toJson(new Date().getTime + 60 * 60 * 1000)) + ("action" -> toJson("first_login"))))
			val url = s"http://127.0.0.1:9000/validation/token/${java.net.URLEncoder.encode(token, "ISO-8859-1")}"
			emailAtiveAccount(email, reVal)
			val o: DBObject = DBObject("token" -> token)
			db.insertObject(o, "authorizationcode", "token")
			(Some(Map("url" -> toJson(url),
					  "name" -> toJson(name),
					  "email" -> toJson(email)
			)), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def checkAuthTokenUsed(data: JsValue)(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val token = (data \ "condition" \ "user_token").asOpt[String].getOrElse(throw new Exception(""))
			val o: DBObject = DBObject("token" -> token)
			db.queryObject(o, "authorizationcode") { x =>
				Map("token" -> toJson(x.getAs[String]("token")))
			} match {
				case None => (Some(Map("used" -> toJson("ok"))), None)
				case _ => throw new Exception("authorizationcode already used")
			}
			
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def authTokenType(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			
			val reVal = (MergeStepResult(data, pr) \ "auth").asOpt[JsValue].map(x => x.as[String Map JsValue]).getOrElse(throw new Exception(""))
			val lst = reVal.get("scope").map(x => x.as[List[String]]).getOrElse(throw new Exception(""))
			if(lst.contains("BD")) (Some(Map("user_type" -> toJson(lst))), None)
			else throw new Exception("user is not BD")
			
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def authCheckTokenAction(data: JsValue)(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			implicit val att = cm.modules.get.get("att").map(x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
			val reVal = (data \ "condition" \ "user_token").asOpt[String].map(x => x).getOrElse(throw new Exception("data not exist"))
			alValidationToken(reVal).validation match {
				case tem => (Some(Map("action" -> toJson(tem.str))), None)
				case _ => throw new Exception("data not exist")
			}
			
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
