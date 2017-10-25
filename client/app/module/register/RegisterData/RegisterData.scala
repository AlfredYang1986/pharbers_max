package module.register.RegisterData

import java.util.Date

import com.mongodb.casbah.Imports.{DBObject, _}
import com.pharbers.cliTraits.DBTrait
import com.pharbers.sercuity.Sercurity
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object regStatus {
    case class regNotified(details: String Map JsValue) extends regStatusDefine(0, "通知")
	case class regApproved(details: String Map JsValue) extends regStatusDefine(1, "同意发送验证码")
	case class regDone(details: String Map JsValue) extends regStatusDefine(2, "用户操作验证码，已经成为用户")
	case class regCommunicated(details: String Map JsValue) extends regStatusDefine(9, "沟通，销售过程")
}

sealed class regStatusDefine(val t : Int, val d : String)

trait RegisterData {
	
	def conditions(data: JsValue): DBObject = {
		val builder = MongoDBObject.newBuilder
		(data \ "condition" \ "reg_id").asOpt[String].map(x => builder += "reg_id" -> x).getOrElse(Unit)
		(data \ "condition" \ "status").asOpt[Int].map(x => builder += "status" -> x).getOrElse(Unit)
		(data \ "condition" \ "email").asOpt[String].map(x => builder += "reg_content.email" -> x).getOrElse(Unit)
		(data \ "condition" \ "phone").asOpt[String].map(x => builder += "reg_content.phone" -> x).getOrElse(Unit)
		builder.result
	}

	implicit val m2d: JsValue => DBObject = { js =>
		val builder = MongoDBObject.newBuilder
		
		val company = (js \ "user" \ "company").asOpt[String].map(x => x).getOrElse(throw new Exception("info input company name"))
		val linkman = (js \ "user" \ "linkman").asOpt[String].map(x => x).getOrElse(throw new Exception("info input linkman name"))
		val email = (js \ "user" \ "email").asOpt[String].map(x => x).getOrElse(throw new Exception("info input email"))
		val phone = (js \ "user" \ "phone").asOpt[String].map(x => x).getOrElse(throw new Exception("info input phone"))
		val status = (js \ "user" \ "status").asOpt[Int].map(x => x).getOrElse(regStatus.regNotified(Map.empty).t)
		val companyPhone = (js \ "user" \ "companyPhone").asOpt[String].map(x => x).getOrElse(Unit)
		val id = (js \ "user" \ "reg_id").asOpt[String].map(x => x).getOrElse(Sercurity.md5Hash(company + email + Sercurity.getTimeSpanWithMillSeconds))

		val reg_content = DBObject(
							"company" -> company,
							"linkman" -> linkman,
							"email" -> email,
							"phone" -> phone,
							"companyPhone" -> companyPhone,
							"scope" -> ("NC" :: Nil))
		
		builder += "reg_id" -> id
		builder += "reg_content" -> reg_content
		builder += "status" -> status
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
			"companyPhone" -> toJson(obj.getAs[String]("companyPhone").map(x => x).getOrElse("")),
			"scope" -> toJson(reg_content.getAs[List[String]]("scope").map(x => x).getOrElse(Nil)),
			"date" -> toJson(obj.getAs[Number]("date").map(x => x).getOrElse(0).toString.toLong))
	}
	def queryRegisterUser(data: JsValue)(implicit db: DBTrait): regStatusDefine = {
		val o = conditions(data)
		db.queryMultipleObject(o, "reg_apply") match {
			case Nil => throw new Exception("user not exist")
			case x :: _ => x.get("status").map(x => x.as[Int]).getOrElse(throw new Exception("data not exist")) match {
				case 0 => regStatus.regNotified(x)
				case 1 => regStatus.regApproved(x)
				case 2 => regStatus.regDone(x)
				case 9 => regStatus.regCommunicated(x)
				case _ => throw new Exception("")
			}
		}
	}
}
