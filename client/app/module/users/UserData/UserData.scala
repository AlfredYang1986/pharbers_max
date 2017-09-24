package module.users.UserData

import java.util.Date

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import com.pharbers.aqll.common.sercurity.Sercurity
import com.pharbers.token.AuthTokenTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait UserData {
	
	def conditions(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "user_id").asOpt[String].map(x => builder += "user_id" -> x).getOrElse(Unit)
		(data \ "email").asOpt[String].map(x => builder += "profile.email" -> x).getOrElse(Unit)
		(data \ "name").asOpt[String].map(x => builder += "profile.name" -> x).getOrElse(Unit)
		(data \ "phone").asOpt[String].map(x => builder += "profile.phone" -> x).getOrElse(Unit)
		builder.result
	}
	
	
	implicit val m2d: (JsValue, AuthTokenTrait) => DBObject = { (js, att) =>
		val builder = MongoDBObject.newBuilder
		
		val jv = (js \ "user_info").asOpt[String] match {
			case None => js
			case Some(one) => (att.decrypt2JsValue(java.net.URLDecoder.decode(one, "UTF-8")) \ "user_info").get
		}
		
		val email = (jv \ "email").asOpt[String].map(x => x).getOrElse(throw new Exception("info input email"))
		val password = (js \ "password").asOpt[String].map(x => x).getOrElse(throw new Exception("")) //忘记密码下，用户其余的信息在token里面，只有密码是单独传送
		val secret = Sercurity.md5Hash(s"$email$password")
		val name = (jv \ "name").asOpt[String].map(x => x).getOrElse(throw new Exception(""))
		val phone = (jv \ "phone").asOpt[String].map(x => x).getOrElse(throw new Exception(""))
		val scope = (jv \ "scope").asOpt[List[String]].map(x => x).getOrElse(Nil)
		val id = (jv \ "user_id").asOpt[String].map(x => x).getOrElse(Sercurity.md5Hash(s"$email"))
		val profile = DBObject("email" -> email, "secret" -> secret, "name" -> name, "phone" -> phone, "scope" -> scope)
		
		builder += "user_id" -> id
		builder += "profile" -> profile
		builder += "other" -> Map.empty
		builder += "date" -> new Date().getTime
		builder.result
	}
	
	implicit val d2m: DBObject => Map[String, JsValue] = { obj =>
		val profile = obj.as[MongoDBObject]("profile")
//		val other = profile.getAs[MongoDBObject]("other").map(x => x).getOrElse("") // TODO: 详细解析留在后面在做，暂时先解析出来不做处理
		Map("user_id" -> toJson(obj.getAs[String]("user_id").map(x => x).getOrElse("")),
			"name" -> toJson(profile.getAs[String]("name").map(x => x).getOrElse("")),
			"email" -> toJson(profile.getAs[String]("email").map(x => x).getOrElse("")),
			"phone" -> toJson(profile.getAs[String]("phone").map(x => x).getOrElse("0")),
			"date" -> toJson(alDateOpt.Timestamp2yyyyMMdd(obj.getAs[Number]("date").getOrElse(0).toString.toLong)),
			"scope" -> toJson(profile.getAs[List[String]]("scope").map(x => x).getOrElse(Nil)))
	}
}
