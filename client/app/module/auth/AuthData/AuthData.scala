package module.auth.AuthData


import com.mongodb.casbah.Imports.{DBObject, _}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait AuthData {

	def jv2m(data: JsValue): Map[String, JsValue] = {
		Map(
			"email" -> toJson((data \ "reginfo" \ "email").asOpt[String].map(x => x).getOrElse("")),
			"name" -> toJson((data \ "reginfo" \ "name").asOpt[String].map(x => x).getOrElse("")),
			"phone" -> toJson((data \ "reginfo" \ "phone").asOpt[String].map(x => x).getOrElse("")),
			"scope" -> toJson((data \ "reginfo" \ "scope").asOpt[List[String]].map(x => x).getOrElse(Nil))
		)
	}
	
	def reg_content(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		
		builder.result
	}
	
	def reg_conditions(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		
		(data \ "email").asOpt[String].map(x => builder += "reg_content.email" -> x).getOrElse(Unit)
		(data \ "name").asOpt[String].map(x => builder += "reg_content.linkman" -> x).getOrElse(Unit)
		(data \ "phone").asOpt[String].map(x => builder += "reg_content.phone" -> x).getOrElse(Unit)
		(data \ "scope").asOpt[List[String]].map(x => builder += "reg_content.scope" -> x).getOrElse(Unit)
		
		builder.result
	}
	
	implicit val m2d: JsValue => DBObject = { js =>
		val builder = MongoDBObject.newBuilder
		val email = (js \ "email").asOpt[String].map(x => x).getOrElse("")
		val pwd = (js \ "password").asOpt[String].map(x => x).getOrElse("")
		builder += "profile.email" -> email
		builder += "profile.secret" -> pwd      //alEncryptionOpt.md5(s"$email$pwd")
		builder.result
	}
	
	implicit val d2m: DBObject => Map[String, JsValue] = { obj =>
		val profile = obj.as[MongoDBObject]("profile")
		Map("email" -> toJson(profile.getAs[String]("email").map(x => x).getOrElse("")),
			"name" -> toJson(profile.getAs[String]("name").map(x => x).getOrElse("")),
			"phone" -> toJson(profile.getAs[String]("phone").map(x => x).getOrElse("")),
			"scope" -> toJson(profile.as[MongoDBList]("scope").toList.asInstanceOf[List[String]]))
	}
	
	def reg_d2m(obj: DBObject) = {
		val reg_content = obj.as[MongoDBObject]("reg_content")
		Map("email" -> toJson(reg_content.getAs[String]("email").map(x => x).getOrElse("")),
			"name" -> toJson(reg_content.getAs[String]("name").map(x => x).getOrElse("")),
			"phone" -> toJson(reg_content.getAs[String]("phone").map(x => x).getOrElse("")),
			"scope" -> toJson(reg_content.as[MongoDBList]("scope").toList.asInstanceOf[List[String]]))
	}
}
