package module.upload

import com.mongodb.casbah.Imports._
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.MergeStepResult
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.upload.UploadMessage._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map

object UploadModule extends ModuleTrait {
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msgQueryWithUserCompanyForUpload(data: JsValue) => queryWithUserCompanyForUpload(data)(pr)
		case _ => throw new Exception("function is not impl")
	}
	
	def queryWithUserCompanyForUpload(data: JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val user_id = (MergeStepResult(data, pr) \ "user" \ "user_id").asOpt[String].getOrElse(throw new Exception(""))
			db.queryObject(DBObject("user_id" -> user_id), "users") { x =>
				val reVal = x.as[MongoDBObject]("profile")
				val company = reVal.getAs[String]("company")
				Map("company" -> toJson(company))
			} match {
				case None => throw new Exception("")
				case Some(one) => (Some(Map("user" -> toJson(one))), None)
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
}
