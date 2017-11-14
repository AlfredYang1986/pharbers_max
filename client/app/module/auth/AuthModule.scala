package module.auth

import java.util.Date

import com.mongodb.casbah.Imports._
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.{MergeStepResult, alValidationToken}
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.message.im.EmChatMsg
import com.pharbers.token.AuthTokenTrait
import module.auth.AuthData._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.toJson
import module.auth.AuthMessage._
import com.pharbers.message.send.SendMessageTrait
import com.pharbers.sercuity.Sercurity

import scala.collection.immutable.Map


object AuthModule extends ModuleTrait with AuthData {
	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case MsgUserAuth(data) => authWithPassword(data)
		case MsgAuthTokenParser(data) => authTokenParser(data)
		case MsgAuthTokenExpire(data) => checkAuthTokenExpire(data)(pr)
		case MsgAuthCreateToken(data) => authCreateToken(data)(pr)
		case MsgAuthCodePushSuccess(data) => authCodePushSuccess(data)
		case MsgAuthTokenType(data) => authTokenType(data)(pr)
		case MsgAuthTokenUsed(data) => checkAuthTokenUsed(data)
		
		case MsgAuthCheckTokenAction(data) => authCheckTokenAction(data)
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
					val uid = Sercurity.md5Hash(one("email").as[String])
					val reVal = o + ("expire_in" -> toJson(date + 60 * 60 * 1000 * 24))
					val auth_token = att.encrypt2Token(toJson(reVal))
					(Some(Map("user_token" -> toJson(auth_token), "uid" -> toJson(uid))), None)
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def authCreateIMUser(data: JsValue)
	                    (pr: Option[String Map JsValue])
	                    (implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val att = cm.modules.get.get("att").map(x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
			
			val vJson = att.decrypt2JsValue(pr.get("user_token").as[String])
			val company = db.queryObject(DBObject("user_id" -> (vJson \ "user_id").as[String]), "users") { obj =>
				val profile = obj.as[MongoDBObject]("profile")
				Map("company" -> toJson(profile.getAs[String]("company").map(x => x).getOrElse("")))
			}.get("company").as[String] // 查询公司名
			
			val reVal = MergeStepResult(data, pr).as[String Map JsValue] - "condition"
			val imUser = s"${company}_${reVal("imuid").as[String]}_${reVal("uid").as[String]}"
			val result = reVal ++ Map("company" -> toJson(company)) ++ Map("imuid" -> toJson(imUser))
			// TODO: 环信的错误处理未加入项目错误列表中，先记着
			EmChatMsg().registerUser(imUser, imUser) // push 环信临时用户
			(Some(result), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	
	def authScanningRoomsAddUser(data: JsValue)
	                            (pr: Option[String Map JsValue])
	                            (implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val company = pr.get("company").as[String]
			
			// TODO：环信错误处理未加入项目错误列表
			(Json.parse(EmChatMsg().getAllRooms) \ "data").as[List[String Map JsValue]]
				.filterNot(x => x("name").as[String] != company + "_" + pr.get("uid").as[String])
				.map(x => x("id").as[String]) match {
					case Nil => Unit
					case lst =>
						val uuid = pr.get("imuid").as[String]
						lst foreach (x => EmChatMsg().setRoomMembers(x, uuid :: Nil))
			}
			(Some(pr.get - "company"), None)
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
			if (lst.contains("BD")) (Some(Map("user_type" -> toJson(lst))), None)
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
