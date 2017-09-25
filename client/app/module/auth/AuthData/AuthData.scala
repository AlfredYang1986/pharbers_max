package module.auth.AuthData

import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait AuthData {
	
	implicit val m2d: JsValue => DBObject = { js =>
		val builder = MongoDBObject.newBuilder
		val email = (js \ "email").asOpt[String].map(x => x).getOrElse("")
		val pwd = (js \ "password").asOpt[String].map(x => x).getOrElse("")
		
		builder += "profile.email" -> email
		builder += "profile.secret" -> alEncryptionOpt.md5(s"$email$pwd")
		
		builder.result()
	}
	
	implicit val d2m: DBObject => Map[String, JsValue] = { obj =>
		val profile = obj.as[MongoDBObject]("profile")
		Map("email" -> toJson(profile.getAs[String]("email").map(x => x).getOrElse("")),
			"name" -> toJson(profile.getAs[String]("name").map(x => x).getOrElse("")),
			"phone" -> toJson(profile.getAs[String]("phone").map(x => x).getOrElse("")),
			"scope" -> toJson(profile.as[MongoDBList]("scope").toList.asInstanceOf[List[String]]))
	}
}
