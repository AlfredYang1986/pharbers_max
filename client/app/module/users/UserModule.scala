package module.users

import java.util.Date

import com.mongodb.casbah.Imports._
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.email.{Mail, StmConf}
import com.pharbers.aqll.common.sercurity.Sercurity
import module.users.UserMessage._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.token.AuthTokenTrait
import module.users.UserData._

import scala.collection.immutable.Map


object UserModule extends ModuleTrait with UserData {
    
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_user_push(data) => push_user(data)(pr)
        case msg_user_delete(data) => delete_user(data)
        case msg_user_update(data) => update_user(data)(pr)
        case msg_user_query(data) => query_user(data)
        case msg_user_query_info(data) => query_user_info(data)

        case msg_user_email_check(data) => check_user_email(data)
        case msg_user_forget_password(data) => forget_password_user(data)(pr)

        case msg_user_token_op(data) => token_op_user(data)(pr)
        case msg_user_chang_pwd(data) => change_user_pwd(data)(pr)
        case msg_user_not_exist(data) => user_not_exist(data)
        
        case _ => throw new Exception("function is not impl")
    }
    
    def push_user(data: JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o = pr match {
                case None => m2d(data)
                case Some(one) =>
                    m2d(one.get("user_info").map(x => x).getOrElse(throw new Exception("data not exist")))
            }
            db.insertObject(o, "users", "user_id")
            (Some(Map("push_user" -> toJson("ok"))), None)
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def delete_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o = conditions(data)
            db.deleteObject(o, "users", "user_id")
            (Some(Map("delete_user" -> toJson("ok"))), None)
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def update_user(data: JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            println(data)
            println(pr)

            val o = pr match {
                case None => m2d(data)
                case Some(one) => m2d(one.get("user_info").map(x => x).getOrElse(throw new Exception("data not exist")))
            }

            println(o)

            db.updateObject(o, "users", "user_id")
            (Some(Map("push_user" -> toJson("ok"))), None)
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def query_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
            val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(20)
            val o = conditions(data)

            db.queryMultipleObject(o, "users", "date", skip, take) match {
                case Nil => throw new Exception("data not exist")
                case lst => (Some(Map("registers" -> toJson(lst))), None)
            }
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def query_user_info(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o = conditions(data)
            db.queryObject(o, "users") match {
                case None => throw new Exception("data not exist")
                case Some(one) => (Some(Map("user_info" -> toJson(one))), None)
            }
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def check_user_email(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o = conditions(data)
            db.queryObject(o, "users") match {
                case None => throw new Exception("data not exist")
                case Some(one) => (Some(Map("info" -> toJson(one))), None)
            }
            
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def forget_password_user(data: JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
        try {
            pr match {
                case None => throw new Exception("pr data not exist")
                case Some(one) =>
                    val map = one.get("info").map(x => x).get.as[Map[String, JsValue]]
                    val reVal = map + ("expire_in" -> toJson(new Date().getTime +  60 * 1000 * 10)) + ("action" -> toJson("forget_password"))
                    val token = java.net.URLEncoder.encode(att.encrypt2Token(toJson(reVal)), "ISO-8859-1")
                    val url = s"http://127.0.0.1:9000/validation/token/$token"
    
                    val email = map.get("email").map(x => x.as[String]).getOrElse("")
//                    val html = views.html.inEmail(email, url)
                    val html = views.html.pwdEmail(email, url)
                    implicit val stm = StmConf()
                    Mail().setContext(html.toString).setSubject("忘记密码").sendTo(email)
                    //原本是一个整个html的，因页面没有所以暂时只做url
                    (Some(Map("urltoken" -> toJson("ok"))), None)
            }
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def token_op_user(data: JsValue)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
                val token = pr match {
                    case None => throw new Exception("pr data not exist")
                    case Some(one) => one.get("auth").map( x => x ).getOrElse(throw new Exception("data not exist"))
                }
            (Some(Map("user_info" -> toJson(token))), None)
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def change_user_pwd(data: JsValue)(pr: Option[Map[String, JsValue]])(implicit cm: CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))

            val user = pr match {
                case None => throw new Exception("pr data not exist")
                case Some(one) => one.get("user_info").map(x => x).getOrElse(throw new Exception("data not exist"))
             }
            val o = m2d(toJson(user.as[Map[String, JsValue]] ++ Map("password" -> toJson((data \ "password").asOpt[String].getOrElse("")))))

            val email = o.getAs[MongoDBObject]("profile").get.getAs[String]("email").get
            val one = db.queryObject(DBObject("profile.email" -> email), "users")(d2m) match {
                case None => {
                    db.insertObject(o, "users", "user_id")
                    o
                }
                case Some(tmp) => {
                    println(tmp)
                    db.updateObject(o, "users", "user_id")
                    o
                }
            }

            val date = new Date().getTime
            val reVal = one + ("expire_in" -> toJson(date + 60 * 60 * 1000 * 24))
			val auth_token = att.encrypt2Token(toJson(reVal))
			(Some(Map("user" -> toJson(one - "scope"), "auth_token" -> toJson(auth_token))), None)

//            (Some(Map("user_info" -> toJson(d2m(o)))), None)
        }catch {
            case ex: Exception =>
                println(ex)
                (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }

    def user_not_exist(data : JsValue)(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
            val db = conn.queryDBInstance("cli").get

            val js = (data \ "reginfo").asOpt[JsValue].get
            val o = conditions(js)
            db.queryObject(o, "users") match {
                case None => (Some(Map("not_exist" -> toJson("ok"))), None)
                case Some(_) => throw new Exception("")
            }
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
}
