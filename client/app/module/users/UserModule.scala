package module.users

import java.util.Date

import com.mongodb.DBObject
import com.pharbers.ErrorCode
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
        case msg_user_push(data) => push_user(data)
        case msg_user_delete(data) => delete_user(data)
        case msg_user_update(data) => update_user(data)
        case msg_user_query(data) => query_user(data)
        case msg_user_query_info(data) => query_user_info(data)

        case msg_user_email_check(data) => check_user_email(data)
        case msg_user_forget_password(data) => forget_password_user(data)(pr)
        
        case _ => throw new Exception("function is not impl")
    }
    
    def push_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o: DBObject = m2d(data, att)
            db.insertObject(o, "users", "user_id")
            (Some(Map("push_user" -> toJson("ok"))), None)
        }catch {
            case ex: Exception =>
                println(ex)
                (None, Some(ErrorCode.errorToJson(ex.getMessage)))
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
    
    def update_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o = m2d(data, att)
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
                case Some(one) => (Some(Map("user_info" -> toJson(one))), None)
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
                    val reVal = one.map(x => x) + ("expire_in" -> toJson(new Date().getTime +  60 * 1000 * 10)) + ("action" -> toJson("forget_password"))
                    val token = java.net.URLEncoder.encode(att.encrypt2Token(toJson(reVal)), "ISO-8859-1")
                    val url = s"http://127.0.0.1:9000/validation/token/$token"
                    //原本是一个整个html的，因页面没有所以暂时只做url
                    (Some(Map("url" -> toJson(url))), None)
            }
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
}
