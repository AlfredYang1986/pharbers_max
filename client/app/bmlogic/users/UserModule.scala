package bmlogic.users

import java.util.{Date, UUID}

import bminjection.db.DBTrait
import bmlogic.MaxEnmeration.UserScope
import bmlogic.common.sercurity.Sercurity
import bmlogic.users.UserData.UserData
import bmlogic.users.UserMessage.msg_PushUserCommand
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import bmutil.MergeJs.dataMergeWithPr
import bmutil.errorcode.ErrorCode
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by yym on 9/14/17.
  */
object UserModule extends ModuleTrait with UserData{
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_PushUserCommand(data) => pushUser(data)(pr)
        
        case _ => ???
    }
    
    
    def pushUser(data :JsValue)
                (pr : Option[Map[String, JsValue]])
                (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val uuid=UUID.randomUUID().toString
            val id = Sercurity.md5Hash(uuid)
            val date = new Date().getTime
            val reg_id = (js \ "reg_id").asOpt[String].map(x => x).getOrElse(throw  new Exception("input error"))
            val res = db.queryObject(MongoDBObject("user_id" -> reg_id), "reg_apple")
            if(res.isEmpty) throw new Exception("unknown error")
            else {
                val profile = res.get.get("reg_content").getOrElse(throw new Exception("unknown error"))
                val user  = res.get - "reg_id" - "date" - "status"  + ("user_id" -> toJson(id))+ ("profile" -> toJson(profile)) + ("date" -> toJson(date))
                db.insertObject(toJson(user), "users", "user_id")
            }
            
            
            (None, Some(toJson(Map("operation" -> "ok"))))
        }catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    def queryUserWithID(data : JsValue)
                           (pr : Option[Map[String, JsValue]])
                           (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val user_id = (js \ "user_id").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            val result = db.queryObject(MongoDBObject("user_id" -> user_id), "users")
            if (result.isEmpty) throw new Exception("unknown error")
            else {
                (Some(Map(
                    "user" -> toJson(result.get)
                )), None)
            }
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def queryAllUsers(data : JsValue)
                         (pr : Option[Map[String, JsValue]])
                         (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val take = (js \ "take").asOpt[Int].map (x => x).getOrElse(20)
            val skip = (js \ "skip").asOpt[Int].map (x => x).getOrElse(0)
            val result = db.queryMultipleObject(MongoDBObject(), "users","date", take,skip)
            if (result.isEmpty) throw new Exception("unknown error")
            else {
                (Some(Map(
                    "users" -> toJson(result)
                )), None)
            }
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    def deleteUser(data : JsValue)
                      (pr : Option[Map[String, JsValue]])
                      (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val user_id = (js \ "user_id").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            val result  = db.queryObject(MongoDBObject("user_id" -> user_id), "users")
            if (result.isEmpty) throw new Exception("unknown error")
            else {
                val del = db.deleteObject(MongoDBObject("user_id" ->user_id), "users", "user_id")
                (Some(Map(
                    "operation" -> toJson("ok")
                )), None)
            }
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
        
    }
    def checkAdminScope(data : JsValue)
                       (pr : Option[Map[String, JsValue]])
                       (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue])={
        try{
            val js = dataMergeWithPr(data,pr)
            val scope = (js  \ "scope" ).asOpt[List[String]].filter(x => x.equals(UserScope.AD))
            var res = pr.get
            if (scope.isEmpty) {
                res = res + ("Warning" -> toJson("没有管理员权限"))
            }
            (Some(res), None)
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def checkBDScope(data : JsValue)
                    (pr : Option[Map[String, JsValue]])
                    (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue])={
        try{
            val js = dataMergeWithPr(data,pr)
            val scope = (js  \ "scope" ).asOpt[List[String]].filter(x => x.equals(UserScope.BD))
            var res = pr.get
            if (scope.isEmpty) {
                res = res + ("Warning" -> toJson("没有BD权限"))
            }
            (Some(res), None)
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    
}
