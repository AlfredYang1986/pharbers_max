package bmlogic.register

import java.util.{Date, UUID}

import bminjection.db.DBTrait
import bmlogic.MaxEnmeration.{RegisterStatus, UserScope}
import bmlogic.common.sercurity.Sercurity
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import bmutil.MergeJs._
import bmutil.errorcode.ErrorCode
import com.mongodb.DBObject
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import bminjection.token.AuthTokenTrait
import bmlogic.register.RegisterMessage._
/**
  * Created by yym on 9/14/17.
  */
object RegisterModule extends ModuleTrait with RegisterData{
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_pushAdminCommand(data) => pushAdmin(data)(pr)
        case msg_pushRegisterWithoutCheckCommand(data) => pushRegisterWithoutCheck(data)(pr)
        case msg_queryRegisterWithIDCommand(data) => queryRegisterWithID(data)(pr)
        case msg_queryAllRegistersCommand(data) => queryAllRegisters(data)(pr)
        case msg_deleteRegisterCommand(data) => deleteRegister(data)(pr)
        case msg_checkRegisterStatusCommand(data) => checkRegisterStatus(data)(pr)
        case msg_cryptRegisterCodeCommand(data) => cryptRegisterCode(data)(pr)
        case msg_decryptRegisterCodeCommand(data) => decryptRegisterCode(data)(pr)
        case msg_checkAuthTokenExpireCommand(data) => decryptRegisterCode(data)(pr)
        case _ => ???
    }
    
    def pushAdmin(data : JsValue)
                 (pr : Option[Map[String, JsValue]])
                 (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val uuid = UUID.randomUUID().toString
            val id = Sercurity.md5Hash(uuid)
            val date = new Date().getTime
            val status = RegisterStatus.approved
            val email = (js \ "email").asOpt[String].map(x => x).getOrElse(throw  new Exception("input error"))
            val phone = (js \ "phone").asOpt[String].map(x => x).getOrElse(throw  new Exception("input error"))
            val other = (js \ "other").asOpt[String].map(x => x).getOrElse("")
            val name = "pharbers"
            val secret = Sercurity.md5Hash("pharbers")
            val scope = List(UserScope.AD)
            val res : DBObject= toJson(Map(
                "reg_id" -> toJson(id),
                "date" -> toJson(date),
                "status" -> toJson(status),
                "reg_content" -> toJson(Map("email" -> toJson(email),
                    "secret" -> toJson(secret),
                    "name" -> toJson(name),
                    "phone" -> toJson(phone),
                    "scope" -> toJson(scope),
                    "other" -> toJson(other)
                    
                    ))
            ))
            db.insertObject(res, "reg_apply", "reg_id")
            (None, Some(toJson(Map("operation" -> "ok"))))
        }catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
      
    }
    
    def pushRegisterWithoutCheck(data : JsValue)
                 (pr : Option[Map[String, JsValue]])
                 (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val uuid = UUID.randomUUID().toString
            val id = Sercurity.md5Hash(uuid)
            val date = new Date().getTime
            val status = RegisterStatus.posted
            val email = (js \ "email").asOpt[String].map(x => x).getOrElse(throw  new Exception("input error"))
            val secret = (js \ "secret").asOpt[String].map(x => x).getOrElse(throw  new Exception("input error"))
            val name = (js \ "name").asOpt[String].map(x => x).getOrElse(throw  new Exception("input error"))
            val phone = (js \ "phone").asOpt[String].map(x => x).getOrElse(throw  new Exception("input error"))
            val scope = (js \ "scope").asOpt[List[String]].map(x => x).getOrElse(throw  new Exception("input error"))
            val other = (js \ "other").asOpt[String].map(x => x).getOrElse("")
            val res : DBObject= toJson(Map(
                "reg_id" -> toJson(id),
                "date" -> toJson(date),
                "status" -> toJson(status),
                "reg_content" -> toJson(Map("email" -> toJson(email),
                    "secret" -> toJson(secret),
                    "name" -> toJson(name),
                    "phone" -> toJson(phone),
                    "scope" -> toJson(scope),
                    "other" -> toJson(other)))
            ))
            db.insertObject(res, "reg_apply", "reg_id")
            (None, Some(toJson(Map("operation" -> "ok"))))
        }catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    
    def queryRegisterWithID(data : JsValue)
                           (pr : Option[Map[String, JsValue]])
                           (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val reg_id = (js \ "reg_id").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            val result = db.queryObject(MongoDBObject("reg_id" -> reg_id), "reg_apply")
            if (result.isEmpty) throw new Exception("unkonwn error")
            else {
                (Some(Map(
                    "register" -> toJson(result.get)
                )), None)
            }
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def queryAllRegisters(data : JsValue)
                         (pr : Option[Map[String, JsValue]])
                         (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val take = (js \ "take").asOpt[Int].map (x => x).getOrElse(20)
            val skip = (js \ "skip").asOpt[Int].map (x => x).getOrElse(0)
            val result = db.queryMultipleObject(MongoDBObject(), "reg_apply","date", take,skip)
            if (result.isEmpty) throw new Exception("unkonwn error")
            else {
                (Some(Map(
                    "registers" -> toJson(result)
                )), None)
            }
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def checkRegisterStatus(data : JsValue)
                     (pr : Option[Map[String, JsValue]])
                     (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val reg_id = (js \ "reg_id").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            val status = (js \ "status").asOpt[String].map(x =>x).getOrElse(throw new Exception("input error"))
            val result  = db.queryObject(MongoDBObject("reg_id" -> reg_id), "reg_apply")
            
            if (result.isEmpty) throw new Exception("unkonwn error")
            else {
                val reg = result.get - "status" + ("status" -> status)
                val up = db.updateObject(reg, "reg_apple", "reg_id")
                (None, Some(toJson(Map("operation" -> "ok"))))
            }
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def deleteRegister(data : JsValue)
                           (pr : Option[Map[String, JsValue]])
                           (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val reg_id = (js \ "reg_id").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            
           
            db.deleteObject(MongoDBObject("reg_id" -> reg_id), "reg_apply", "reg_id")
            (None, Some(toJson(Map("operation" -> "ok"))))
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def cryptRegisterCode(data : JsValue)
                      (pr : Option[Map[String, JsValue]])
                      (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
            val date = new Date().getTime
            val scre = registerSeed(data) + "expire_in" -> toJson(date + 60 * 60 * 1000 * 24 * 1)
            val registerCode =att.encrypt2Token(toJson(Map(scre)))
            (None, Some(toJson(Map("registerCode" -> registerCode))))
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def decryptRegisterCode(data : JsValue)
               (pr : Option[Map[String, JsValue]])
               (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val js = dataMergeWithPr(data, pr)
            val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
            val registerCode=(js \ "registerCode").asOpt[String].getOrElse(throw  new Exception("inoput error"))
            val registerInfo = att.decrypt2JsValue(registerCode)
            (None, Some(toJson(Map("registerCode" -> registerInfo))))
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def checkAuthTokenExpire(data : JsValue)
                            (pr : Option[Map[String, JsValue]])
                            (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        
        try {
            val mergeJs=dataMergeWithPr(data,pr)
            val expire_in = (mergeJs \ "registerCode" \ "expire_in").asOpt[Long].map (x => x).getOrElse(throw new Exception("token parse error"))
            
            if (new Date().getTime > expire_in) throw new Exception("token expired")
            else (pr, None)
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    

    
    def registerSeed(js : JsValue) : JsValue = {
        val  res = js + "seed" -> "pharbers"
        toJson(Map(res))
    }
    
}
