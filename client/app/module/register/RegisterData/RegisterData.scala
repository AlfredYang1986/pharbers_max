package module.register.RegisterData

import java.util.Date

import com.mongodb.casbah.Imports.{DBObject, _}
import com.pharbers.sercuity.Sercurity
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object regStatus {
    object regNotified extends regStatusDefine(0, "通知")
    object regApproved extends regStatusDefine(1, "同意发送验证码")
    object regDone extends regStatusDefine(2, "用户操作验证码，已经成为用户")

    object regCommunicated extends regStatusDefine(9, "沟通，销售过程")
    object regExpired extends regStatusDefine(10, "过期了")
}

sealed case class regStatusDefine(val t : Int, val d : String)

trait RegisterData {
	
	def conditions(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "reg_id").asOpt[String].map(x => builder += "reg_id" -> x).getOrElse("")
		(data \ "condition" \ "email").asOpt[String].map(x => builder += "reg_content.email" -> x).getOrElse("")
		(data \ "condition" \ "phone").asOpt[String].map(x => builder += "reg_content.phone" -> x).getOrElse("")
		builder.result
	}

	implicit val m2d: JsValue => DBObject = { js =>
		val builder = MongoDBObject.newBuilder
		
		val company = (js \ "user" \ "company").asOpt[String].map(x => x).getOrElse(throw new Exception("info input company name"))
		val linkman = (js \ "user" \ "linkman").asOpt[String].map(x => x).getOrElse(throw new Exception("info input linkman name"))
		val email = (js \ "user" \ "email").asOpt[String].map(x => x).getOrElse(throw new Exception("info input email"))
		val phone = (js \ "user" \ "phone").asOpt[String].map(x => x).getOrElse(throw new Exception("info input phone"))
		val id = Sercurity.md5Hash(company + email + Sercurity.getTimeSpanWithMillSeconds)
		
		val reg_content = Map(
							"company" -> company,
							"linkman" -> linkman,
							"email" -> email,
							"phone" -> phone,
							"scope" -> ("NC" :: Nil))

		builder += "reg_id" -> id
		builder += "reg_content" -> reg_content
		builder += "status" -> regStatus.regNotified.t // 0.asInstanceOf[Number]
		builder += "date" -> new Date().getTime
		
		builder.result
	}
	
	implicit val d2m: DBObject => Map[String, JsValue] = { obj =>
		val reg_content = obj.as[MongoDBObject]("reg_content")
		Map("reg_id" -> toJson(obj.getAs[String]("reg_id").map(x => x).getOrElse("")),
			"email" -> toJson(reg_content.getAs[String]("email").map(x => x).getOrElse("")),
			"company" -> toJson(reg_content.getAs[String]("company").map(x => x).getOrElse("")),
			"name" -> toJson(reg_content.getAs[String]("linkman").map(x => x).getOrElse("")),
			"phone" -> toJson(reg_content.getAs[String]("phone").map(x => x).getOrElse("")),
			"status" -> toJson(obj.getAs[Int]("status").map(x => x).getOrElse(0)),
			"scope" -> toJson(reg_content.getAs[List[String]]("scope").map(x => x).getOrElse(Nil)),
			"date" -> toJson(obj.getAs[Number]("date").map(x => x).getOrElse(0).toString.toLong))
	}
}
