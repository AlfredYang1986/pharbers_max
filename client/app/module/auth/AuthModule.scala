package module.auth

import java.util.Date

import com.mongodb.casbah.Imports.{MongoDBList, MongoDBObject}
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports._
import module.auth.AuthMessage._

import scala.collection.immutable.Map


object AuthModule extends ModuleTrait {
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_user_auth(data) => authWithPassword(data)
		case msg_auth_token_parser(data) => authTokenParser(data)
		case msg_auth_token_expire(data) => checkAuthTokenExpire(data)(pr)
		case _ => ???
	}
	
	def authWithPassword(data : JsValue)(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
		implicit val db = conn.queryDBInstance("cli").get
		val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
		try {
			val email = (data \ "email").asOpt[String].getOrElse("")
			val pwd = (data \ "password").asOpt[String].getOrElse("")
			val map = Map("profile.email" -> email, "profile.secret" -> alEncryptionOpt.md5(s"$email$pwd"))
			val date = new Date().getTime
			val result = db.queryObject(map, "users") { x =>
				val profile = x.as[MongoDBObject]("profile")
				val name = profile.getAs[String]("name").getOrElse("")
				val phone = profile.getAs[String]("phone").getOrElse("")
				val scope = profile.as[MongoDBList]("scope").toList.asInstanceOf[List[String]]
				Map("email" -> toJson(email),
					"name" -> toJson(name),
					"phone" -> toJson(phone),
					"scope" -> toJson(scope))
			}
			if (result.isEmpty) throw new Exception("unkonwn error")
			else {
				val reVal = result.get + ("expire_in" -> toJson(date + 60 * 60 * 1000 * 24))
				val auth_token = att.encrypt2Token(toJson(reVal))
				(Some(Map("user" -> toJson(result.map(x => x - "scope")), "auth_token" -> toJson(auth_token))), None)
			}
		}catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def checkAuthTokenExpire(data : JsValue)
	                        (pr : Option[Map[String, JsValue]])
	                        (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val auth = pr.map (x => x.get("auth").get).getOrElse(throw new Exception("token parse error"))
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
			val auth_token = (data \ "token").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
			val auth = att.decrypt2JsValue(auth_token)
			(Some(Map("auth" -> auth)), None)
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
