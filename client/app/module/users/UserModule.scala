package module.users

import com.mongodb.DBObject
import com.pharbers.ErrorCode
import module.users.UserMessage._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.users.UserData._


object UserModule extends ModuleTrait with UserData {
    
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_user_push(data) => push_user(data)
        case msg_user_delete(data) => delete_user(data)
        case msg_user_update(data) => update_user(data)
        case msg_user_query(data) => query_user(data)
        case msg_user_query_info(data) => query_user_info(data)
        case _ => throw new Exception("function is not impl")
    }
    
    def push_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o: DBObject = m2d(data)
            db.insertObject(o, "users", "user_id")
            (None, Some(toJson(Map("push_user" -> "ok"))))
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
            (None, Some(toJson(Map("delete_user" -> "ok"))))
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def update_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o = m2d(data)
            db.updateObject(o, "users", "user_id")
            (None, Some(toJson(Map("push_user" -> "ok"))))
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
            val result = db.queryObject(o, "users")
            (None, Some(toJson(Map("info" -> toJson(result)))))
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
}
