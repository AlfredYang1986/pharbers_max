package module.register.RegisterData

import java.util.Date

import com.mongodb.casbah.Imports.{DBObject, _}
import com.pharbers.aqll.common.MaxEnmeration.alRegisterStatus
import com.pharbers.sercuity.Sercurity
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait RegisterData {
	
	def conditions(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "reginfo" \ "reg_id").asOpt[String].map(x => builder += "reg_id" -> x).getOrElse("")
		(data \ "reginfo" \ "email").asOpt[String].map(x => builder += "reg_content.email" -> x).getOrElse("")
		(data \ "reginfo" \ "phone").asOpt[String].map(x => builder += "reg_content.phone" -> x).getOrElse("")
		builder.result
	}

	implicit val m2d: JsValue => DBObject = { js =>
		val builder = MongoDBObject.newBuilder
		
		val company = (js \ "reginfo" \ "company").asOpt[String].map(x => x).getOrElse(throw new Exception("info input company name"))
		val linkman = (js \ "reginfo" \ "linkman").asOpt[String].map(x => x).getOrElse(throw new Exception("info input linkman name"))
		val email = (js \ "reginfo" \ "email").asOpt[String].map(x => x).getOrElse(throw new Exception("info input email"))
		val phone = (js \ "reginfo" \ "phone").asOpt[String].map(x => x).getOrElse(throw new Exception("info input phone"))
		val id = Sercurity.md5Hash(company + email + Sercurity.getTimeSpanWithMillSeconds)
		
		val reg_content = Map(
							"company" -> company,
							"linkman" -> linkman,
							"email" -> email,
							"phone" -> phone,
							"scope" -> ("NC" :: Nil))

		builder += "reg_id" -> id
		builder += "reg_content" -> reg_content
		builder += "status" -> alRegisterStatus.posted.id
		builder += "date" -> new Date().getTime
		
		builder.result
	}
	
	implicit val d2m: DBObject => Map[String, JsValue] = { obj =>
		val reg_content = obj.as[MongoDBObject]("reg_content")
		Map("email" -> toJson(reg_content.getAs[String]("email").map(x => x).getOrElse("")),
			"company" -> toJson(reg_content.getAs[String]("company").map(x => x).getOrElse("")),
			"name" -> toJson(reg_content.getAs[String]("linkman").map(x => x).getOrElse("")),
			"phone" -> toJson(reg_content.getAs[String]("phone").map(x => x).getOrElse("")),
			"status" -> toJson(obj.getAs[Int]("status").map(x => x).getOrElse(0)),
			"scope" -> toJson(reg_content.getAs[List[String]]("scope").map(x => x).getOrElse(Nil)),
			"date" -> toJson(obj.getAs[Number]("date").map(x => x).getOrElse(0).toString.toLong))
	}
}
