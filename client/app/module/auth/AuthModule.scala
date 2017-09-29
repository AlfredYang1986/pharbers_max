package module.auth

import java.util.Date

import com.pharbers.ErrorCode
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import module.auth.AuthData._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.auth.AuthMessage._
import com.pharbers.aqll.common.email.{Mail, StmConf}

import scala.collection.immutable.Map


object AuthModule extends ModuleTrait with AuthData {
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_user_auth(data) => authWithPassword(data)
		case msg_auth_token_parser(data) => authTokenParser(data)
		case msg_auth_token_expire(data) => checkAuthTokenExpire(data)(pr)
		case msg_auth_create_token(data) => authCreateToken(data)(pr)
		case msg_auth_token_defeat(data) => authWithTokenDefeat(data)
		case msg_auth_code_push_success(data) => authCodePushSuccess(data)
		case _ => throw new Exception("function is not impl")
	}
	
	def authWithPassword(data : JsValue)(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
		val db = conn.queryDBInstance("cli").get
		val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
		try {
			val map = m2d(data)
			val date = new Date().getTime
			db.queryObject(map, "users") match {
				case None => throw new Exception("data not exist")
				case Some(one) =>
					val reVal = one + ("expire_in" -> toJson(date + 60 * 60 * 1000 * 24))
					val auth_token = att.encrypt2Token(toJson(reVal))
					(Some(Map("user" -> toJson(one - "scope"), "auth_token" -> toJson(auth_token))), None)
			}
		}catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def checkAuthTokenExpire(data : JsValue)
	                        (pr : Option[Map[String, JsValue]])
	                        (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val auth = pr match {
				case None => throw new Exception("pr data not exist")
				case Some(one) => one.get("auth").map(x => x).getOrElse(throw new Exception("token parse error"))
			}
			val expire_in = (auth \ "expire_in").asOpt[Long].map (x => x).getOrElse(throw new Exception("token parse error"))
			if (new Date().getTime > expire_in) throw new Exception("token expired")
			else (pr, None)
		}catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def authTokenParser(data: JsValue)(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
		try {
			val auth_token = (data \ "user_token").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
			val auth = att.decrypt2JsValue(auth_token)
			(Some(Map("auth" -> auth)), None)
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def authCreateToken(data: JsValue)(pr : Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
		try {
			val o = pr match {
				case None => jv2m(data)
				case Some(one) => jv2m(toJson(Map("reginfo" -> one.get("apply").get)))
			}
			val reVal = att.encrypt2Token(toJson(o + ("expire_in" -> toJson(new Date().getTime + 60 * 60 * 1000))))
			val email = o.get("email").map(x => x.as[String]).getOrElse("")
			val html = views.html.authcode(email, reVal)
			Mail().setContext(html.toString).setSubject("授权码").sendTo(email)(StmConf())
			// 直接返回一个授权码
			(Some(Map("apply" -> toJson(o), "token" -> toJson(reVal))), None)
		}catch {
			case ex: Exception => ex.printStackTrace(); (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def authWithTokenDefeat(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
		val db = conn.queryDBInstance("cli").get
		try{
			val o = reg_conditions(data)
			db.queryObject(o, "reg_apply")(reg_d2m) match {
				case None => throw new Exception("data not exist")
				case Some(one) => println(one); (Some(Map("apply" -> toJson(one))), None)
			}
		}catch {
			case ex: Exception => (None, Some(toJson(ErrorCode.errorToJson(ex.getMessage))))
		}
	}
	
	def authCodePushSuccess(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
		try {
			val token = (data \ "user_token").asOpt[String].map(x => x).getOrElse("")
			val js = att.decrypt2JsValue(token)
			val email = (js \ "email").asOpt[String].map(x => x).getOrElse(throw new Exception("data not exit"))
			val name = (js \ "name").asOpt[String].map(x => x).getOrElse(throw new Exception("data not exit"))
			val reVal = att.encrypt2Token(toJson(js.as[Map[String, JsValue]] + ("expire_in" -> toJson(new Date().getTime + 60 * 60 * 1000)) + ("action" -> toJson("first_login"))))
			val url = s"http://127.0.0.1:9000/validation/token/${java.net.URLEncoder.encode(reVal, "ISO-8859-1")}"
			val html = views.html.inEmail(email, url)
			implicit val stm = StmConf()
			Mail().setContext(html.toString).setSubject("登入链接").sendTo(email)
			(Some(Map("url" -> toJson(url),
				"name" -> toJson(name),
				"email" -> toJson(email)
			)), None)
		}catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
