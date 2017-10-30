package module.users.UserData

import java.util.Date

import com.mongodb.casbah.Imports._
import com.pharbers.aqll.common.alDate.scala.alDateOpt
import com.pharbers.cliTraits.DBTrait
import com.pharbers.message.send.{EmailResetPasswordType, SendMessageTrait}
import com.pharbers.sercuity.Sercurity
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait UserData {
	
	def conditions(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "user_id").asOpt[String].map(x => builder += "user_id" -> x).getOrElse(Unit)
		(data \ "condition" \ "email").asOpt[String].map(x => builder += "profile.email" -> x).getOrElse(Unit)
		(data \ "condition" \ "name").asOpt[String].map(x => builder += "profile.name" -> x).getOrElse(Unit)
		(data \ "condition" \ "phone").asOpt[String].map(x => builder += "profile.phone" -> x).getOrElse(Unit)
		builder.result
	}

	def conditions2(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "reginfo" \ "email").asOpt[String].map(x => builder += "email" -> x).getOrElse(Unit)
		builder.result
	}
	
	
	implicit val m2d: JsValue => DBObject = { js =>
		val builder = MongoDBObject.newBuilder
		val email = (js \ "user" \ "email").asOpt[String].map(x => x).getOrElse(throw new Exception("info input email"))
		val password = (js \ "user" \ "password").asOpt[String].map(x => x).getOrElse(email)
		val name = (js \ "user" \ "name").asOpt[String].map(x => x).getOrElse(throw new Exception("info input linkman name"))
		val phone = (js \ "user" \ "phone").asOpt[String].map(x => x).getOrElse(throw new Exception("info input phone"))
		val companyPhone = (js \ "user" \ "companyPhone").asOpt[String].map(x => x).getOrElse(Unit)
		val companyAddress = (js \ "user" \ "companyAddress").asOpt[String].map(x => x).getOrElse(Unit)
		val scope = (js \ "user" \ "scope").asOpt[List[String]].map(x => x).getOrElse(Nil)
		val id = (js \ "user" \ "user_id").asOpt[String].map(x => x).getOrElse(Sercurity.md5Hash(s"$email"))
		val profile = DBObject("email" -> email, "secret" -> password, "name" -> name, "phone" -> phone, "companyPhone" -> companyPhone, "companyAddress"-> companyAddress,"scope" -> scope)
		
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
			"companyPhone" -> toJson(profile.getAs[String]("companyPhone").map(x => x).getOrElse("")),
			"companyAddress" -> toJson(profile.getAs[String]("companyAddress").map(x => x).getOrElse("")),
			"date" -> toJson(alDateOpt.Timestamp2yyyyMMdd(obj.getAs[Number]("date").getOrElse(0).toString.toLong)),
			"scope" -> toJson(profile.getAs[List[String]]("scope").map(x => x).getOrElse(Nil)))
	}
	
	def emailResetPassword(email: String, token: String)(implicit msg: SendMessageTrait, db: DBTrait): String = {
		// TODO: 写成配置文件
		val url = s"http://127.0.0.1:9000/validation/token/${java.net.URLEncoder.encode(token, "ISO-8859-1")}"
		val html = views.html.emailContent.resetPassword(email, url)
		msg.sendMailMessage(email, EmailResetPasswordType()).sendHtmlMail.setSubTheme("忘记密码").setContext(html.toString).sendToEmail
	}
}
