package module.users

import java.util.{Date, UUID}

import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.DBConection
import module.users.UserMessage._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.common.MergeJs._
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.sercurity.Sercurity
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager


object UserModule extends ModuleTrait {
    
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_user_push(data) => push_user(data)
        case msg_user_delete(data) => delete_user(data)
        case msg_user_query(data) => query_user(data)
        case _ => ???
    }
    
    def push_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        implicit val db = conn.queryDBInstance("cli").get
        try {
            val id = alEncryptionOpt.md5(UUID.randomUUID().toString)
            val email = (data \ "email").asOpt[String].getOrElse(new Exception("info input email"))
            val password = (data \ "password").asOpt[String].getOrElse(new Exception(""))
            val secret = alEncryptionOpt.md5(s"$email$password")
            val name = (data \ "name").asOpt[String].getOrElse(new Exception(""))
            val phone = (data \ "phone").asOpt[String].getOrElse(new Exception(""))
            val profile = Map("email" -> email,
                              "secret" -> secret,
                              "name" -> name,
                              "phone" -> phone,
                              "scope" -> (Nil))
            val users: DBObject = Map("user_id" -> id,
                                      "profile" -> profile,
                                      "other" -> Map.empty,
                                      "date" -> new Date().getTime)
            db.insertObject(users, "users", "user_id")
            (None, Some(toJson(Map("push_user" -> "ok"))))
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def delete_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        implicit val db = conn.queryDBInstance("cli").get
        try {
            val user_id = (data \ "user_id").asOpt[String].getOrElse(new Exception(""))
            val map = Map("user_id" -> user_id)
            db.deleteObject(map, "users", "user_id")
            (None, Some(toJson(Map("delete_user" -> "ok"))))
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def update_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
        
            (None, Some(toJson(Map("push_user" -> "ok"))))
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def query_user(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        implicit val db = conn.queryDBInstance("cli").get
        try {
            val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
            val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(20)
            val map: MongoDBObject = MongoDBObject()

            val result = db.queryMultipleObject(map, "users", "date", skip, take) { x =>
                val profile = x.as[MongoDBObject]("profile")
                val email = profile.getAs[String]("email").getOrElse("")
                val name = profile.getAs[String]("name").getOrElse("")
                val scope = profile.getAs[List[String]]("scope").getOrElse(Nil)
                val phone = profile.getAs[String]("phone").getOrElse("0")
                val other = profile.getAs[MongoDBObject]("other").getOrElse("") // TODO: 详细解析留在后面在做，暂时先解析出来不做处理
                val date = x.getAs[Number]("date").getOrElse(0).toString.toLong
                Map("name" -> toJson(name),
                    "email" -> toJson(email),
                    "phone" -> toJson(phone),
                    "date" -> toJson(date),
                    "scope" -> toJson(scope))
            }
            if (result.isEmpty) throw new Exception("unkonwn error")
            else (Some(Map("registers" -> toJson(result))), None)
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def query_user_info(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            
            (None, Some(toJson(Map("push_user" -> "ok"))))
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
}
