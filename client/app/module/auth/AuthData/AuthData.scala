package module.auth.AuthData


import com.mongodb.casbah.Imports.{DBObject, _}
import com.pharbers.cliTraits.DBTrait
import com.pharbers.message.send.{EmailActiveCodeType, EmailAuthCodeType, SendMessageTrait}
import com.typesafe.config.ConfigFactory
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait AuthData {
	val URL = ConfigFactory.load("URL")

	def jv2m(data: JsValue): Map[String, JsValue] = {
		Map(
			"user_id" -> toJson((data \ "condition" \ "user_id").asOpt[String].map(x => x).getOrElse("")),
			"email" -> toJson((data \ "condition" \ "reginfo" \ "email").asOpt[String].map(x => x).getOrElse(
				(data \ "condition" \ "user" \ "email").asOpt[String].map(x => x).getOrElse("")
			)),
			"name" -> toJson((data \ "condition" \ "user" \ "linkman").asOpt[String].map(x => x).getOrElse(
				(data \ "condition" \ "user" \ "name").asOpt[String].map(x => x).getOrElse("")
			)),
			"phone" -> toJson((data \ "condition" \ "user" \ "phone").asOpt[String].map(x => x).getOrElse("")),
			"scope" -> toJson((data \ "condition" \ "user"  \ "scope").asOpt[List[String]].map(x => x).getOrElse(Nil))
		)
	}
	
	def reg_conditions(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		
		(data \ "condition" \ "email").asOpt[String].map(x => builder += "reg_content.email" -> x).getOrElse(Unit)
//		(data \ "name").asOpt[String].map(x => builder += "reg_content.linkman" -> x).getOrElse(Unit)
//		(data \ "phone").asOpt[String].map(x => builder += "reg_content.phone" -> x).getOrElse(Unit)
//		(data \ "scope").asOpt[List[String]].map(x => builder += "reg_content.scope" -> x).getOrElse(Unit)
		
		builder.result
	}
	
	implicit val m2d: JsValue => DBObject = { js =>
		val builder = MongoDBObject.newBuilder
		val email = (js \ "condition" \ "email").asOpt[String].map(x => x).getOrElse("")
		val pwd = (js \ "condition" \ "password").asOpt[String].map(x => x).getOrElse("")
		builder += "profile.email" -> email
		builder += "profile.secret" -> pwd
		builder.result
	}
	
	implicit val d2m: DBObject => Map[String, JsValue] = { obj =>
		val profile = obj.as[MongoDBObject]("profile")
		Map("user_id" -> toJson(obj.getAs[String]("user_id").map(x => x).getOrElse("")),
			"email" -> toJson(profile.getAs[String]("email").map(x => x).getOrElse("")),
			"name" -> toJson(profile.getAs[String]("name").map(x => x).getOrElse("")),
			"company" -> toJson(profile.getAs[String]("company").map(x => x).getOrElse("")),
			"showCompany" -> toJson(profile.getAs[String]("showCompany").map(x => x).getOrElse("")),
			"phone" -> toJson(profile.getAs[String]("phone").map(x => x).getOrElse("")),
			"scope" -> toJson(profile.as[MongoDBList]("scope").toList.asInstanceOf[List[String]]))
	}

	implicit val r2m: Map[String, String] => Map[String, JsValue] = { obj =>
		Map("email" -> toJson(obj.get("email").map(x => x).getOrElse("")),
			"name" -> toJson(obj.get("name").map(x => x).getOrElse("")),
			"user_id" -> toJson(obj.get("user_id").map(x => x).getOrElse("")),
			"phone" -> toJson(obj.get("phone").map(x => x).getOrElse("")),
			"scope" -> toJson(obj.get("scope").map(x => x.slice(5,x.size-1).split(",").toList)))
	}
	
	def reg_d2m(obj: DBObject): String Map JsValue = {
		val reg_content = obj.as[MongoDBObject]("reg_content")
		Map("reg_id" -> toJson(obj.getAs[String]("reg_id").map(x => x).getOrElse("")),
			"email" -> toJson(reg_content.getAs[String]("email").map(x => x).getOrElse("")),
			"name" -> toJson(reg_content.getAs[String]("linkman").map(x => x).getOrElse("")),
			"phone" -> toJson(reg_content.getAs[String]("phone").map(x => x).getOrElse("")),
			"scope" -> toJson(reg_content.as[MongoDBList]("scope").toList.asInstanceOf[List[String]]))
	}
	
	def emailAuthCode(email: String, token: String)(implicit msg: SendMessageTrait, db: DBTrait): String = {
		// TODO： 改成配置文件
		val url = URL.getString("URL.registerCode")
		val html = views.html.emailContent.authcode(url, email, token)
		msg.sendMailMessage(email, EmailAuthCodeType()).sendHtmlMail.setSubTheme("授权码").setContext(html.toString).sendToEmail
	}
	
	def emailAtiveAccount(email: String, token: String)(implicit msg: SendMessageTrait, db: DBTrait): String = {
		// TODO： 改成配置文件
		val url = s"${URL.getString("URL.validation")}${java.net.URLEncoder.encode(token, "ISO-8859-1")}"
		val html = views.html.emailContent.activeAccount(email, url)
		msg.sendMailMessage(email, EmailActiveCodeType()).sendHtmlMail.setSubTheme("快速登入").setContext(html.toString).sendToEmail
	}
}
