package module.register

import com.mongodb.casbah.Imports._
import play.api.libs.json.{JsString, JsValue}
import play.api.libs.json.Json.toJson
import com.pharbers.ErrorCode
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.cliTraits.DBTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import module.register.RegisterData._
import module.register.RegisterMessage._
import scala.collection.immutable.Map

object RegisterModule extends ModuleTrait with RegisterData {

	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_check_user_is_apply(data) => check_user_is_apply(data)
		case msg_user_apply(data: JsValue) => user_apply(data)(pr)
		case msg_query_apply_user(data: JsValue) => bd_query_apply_user(data)
		case msg_update_apply_user(data: JsValue) => bd_update_apply_user(data)
		case msg_register_token_create(data: JsValue) => user_register_create_token(data)(pr)
		case msg_approve_reg(data: JsValue) => approve_reg(data)(pr)

		case msg_register_token_defeat(data: JsValue) => user_register_token_defeat(data)
		case msg_first_push_user(data: JsValue) => user_first_push(data)(pr)
		case msg_delete_registerUser(data : JsValue) => delete_register(data)

		case _ => throw new Exception("function is not impl")
	}

	def check_user_is_apply(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val os: List[DBObject] = apply_conditions(data)
			val result = os.map { o =>
				db.queryMultipleObject(o, "reg_apply") match {
					case Nil => (0, "")
					case one :: Nil => (one("status").as[Int], one("reg_id").asInstanceOf[JsString].value)
					case _ => throw new Exception("user is repeat")
				}
			}.distinct.find(_._2 != "") match {
				case Some(s) => s
				case None => (0, "")
			}
			(Some(Map( "status" -> toJson(result._1), "result" -> toJson(result._2))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def user_apply(data: JsValue)(pr: Option[Map[String, JsValue]])
					 (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val o: DBObject = data
			val name = (data \ "user").asOpt[String].map(x => x).getOrElse("")

			pr.get.get("status") match {
				case Some(s) if s.as[Int] == 0 => Unit
				case Some(s) if s.as[Int] == 9 => throw new Exception("successful application discuss")
				case Some(s) if s.as[Int] == 1 => throw new Exception("successful application")
				case Some(s) if s.as[Int] == 2 => throw new Exception("successful application please login")
				case _ => throw new Exception("function is not impl")
			}

			pr.get.get("result") match {
				case Some(s) if s.as[JsString].value == "" =>
					db.insertObject(o, "reg_apply", "reg_id")
				case Some(s) =>
					val regId = s.asInstanceOf[JsString].value
					val status = regStatus.regNotified(Map.empty).t.asInstanceOf[Number]
					o += "reg_id" -> regId
					o += "status" -> status
					db.updateObject(o, "reg_apply", "reg_id")
				case None => throw new Exception("user is repeat")
			}

			(Some(Map("user" -> toJson(name))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def bd_query_apply_user(data: JsValue)(implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			val skip = (data \ "skip").asOpt[Int].map(x => x).getOrElse(0)
			val take = (data \ "take").asOpt[Int].map(x => x).getOrElse(20)
			val o = conditions(data)
			db.queryMultipleObject(o, "reg_apply", "date", skip, take) match {
				case Nil => (Some(Map("registers" -> toJson(""))), None)
				case lst => (Some(Map("registers" -> toJson(lst))), None)
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def bd_update_apply_user(data: JsValue)(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get
			db.updateObject(data, "reg_apply", "reg_id")
			(Some(Map("reg_user" -> toJson("ok"))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def user_register_create_token(data: JsValue)(pr: Option[Map[String, JsValue]])
								  (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			implicit val db = conn.queryDBInstance("cli").get
			val o: DBObject = data

			pr.get.get("status") match {
				case Some(s) if s.as[Int] == 0 => Unit
				case Some(s) if s.as[Int] == 9 => Unit
				case Some(s) if s.as[Int] == 1 => throw new Exception("successful application")
				case Some(s) if s.as[Int] == 2 => throw new Exception("successful application please login")
				case _ => throw new Exception("function is not impl")
			}

			pr.get.get("result") match {
				case Some(s) if s.as[JsString].value == "" =>
					o += "status" -> regStatus.regApproved(Map.empty).t.asInstanceOf[Number]
					db.insertObject(o, "reg_apply", "reg_id")
				case Some(s) =>
					val regId = s.asInstanceOf[JsString].value
					val status = regStatus.regApproved(Map.empty).t.asInstanceOf[Number]
					o += "reg_id" -> regId
					o += "status" -> status
					db.updateObject(o, "reg_apply", "reg_id")
				case None => throw new Exception("user is repeat")
			}

			(Some(Map("user_id" -> toJson(o("reg_id")))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def approve_reg(data: JsValue)
				   (pr: Option[Map[String, JsValue]])
				   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = conn.queryDBInstance("cli").get

			val app = pr.get("apply")

			val email = (app \ "email").asOpt[String].get
			val name = (app \ "name").asOpt[String].get

			db.queryObject(DBObject("reg_content.email" -> email, "reg_content.linkman" -> name), "reg_apply") { x =>
				db.updateObject(x, "reg_apply", "reg_id")
				x
			}
			(Some(Map("condition" -> toJson(email))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def user_register_token_defeat(data: JsValue)(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			implicit val db = conn.queryDBInstance("cli").get
			val o: DBObject = conditions(data)
			
			queryRegisterUser(data) match {
				case regStatus.regNotified(_) => throw new Exception("successful application fail BD or AD")
				case regStatus.regApproved(details) => (Some(Map("apply" -> toJson(details))), None)
				case regStatus.regDone(_) => throw new Exception("successful application please login")
				case regStatus.regCommunicated(details) => throw new Exception("successful application discuss")
			}
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def delete_register(data : JsValue)(implicit cm:CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try{
			val con = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val db = con.queryDBInstance("cli").get
			val o : DBObject = m2d(data)
			db.deleteObject(o, "reg_apply", "reg_id")
			val name = (data \ "user").asOpt[String].map(x => x).getOrElse("")
			(Some(Map("user" -> toJson(name))), None)
		}catch {
			case ex : Exception =>
				println(ex)
				(None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
		
	}

	
	
	def user_first_push(data: JsValue)(pr: Option[String Map JsValue])(implicit cm: CommonModules): (Option[String Map JsValue], Option[JsValue]) = {
		try {
			val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
			val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
			val db = conn.queryDBInstance("cli").get
			
			val app = pr.get("user_token")

			val token = app.as[String]
			val reVal = att.decrypt2JsValue(token)
			val regid = (reVal \ "user_id").as[String]
			db.queryObject(DBObject("reg_id" -> regid), "reg_apply") { x =>
				x += "status" -> 2.asInstanceOf[Number]
				db.updateObject(x, "reg_apply", "reg_id")
				x
			}
			
			(Some(pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
}
