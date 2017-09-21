package module.login

import com.pharbers.ErrorCode
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.login.LoginMessage.msg_user_login
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object LoginModule extends ModuleTrait{
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_user_login(data) => user_login(data)
		case _ => ???
	}
	
	def user_login(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
		implicit val db = conn.queryDBInstance("cli").get
		try {
			//TODO : 先写死，后续加上加密
			val email = (data \ "email").asOpt[String].getOrElse("")
			val pwd = (data \ "password").asOpt[String].getOrElse("")
			val map = Map("profile.email" -> email, "profile.secret" -> alEncryptionOpt.md5(s"$email$pwd"))
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
			// TODO: 修改提示信息
			if (result.isEmpty) throw new Exception("unkonwn error")
			else (None, Some(toJson(Map("login" -> toJson(result)))))
		}catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
