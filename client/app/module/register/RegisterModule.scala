package module.register

import java.util.{Date, UUID}

import com.mongodb.DBObject
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import bmlogic.register.RegisterMessage._
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.DBConection
import com.pharbers.aqll.common.MaxEnmeration.{RegisterStatus, UserScope}
import com.pharbers.aqll.common.MergeJs._
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.sercurity.Sercurity
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.cliTraits.DBTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.mongodbConnect.from
import com.pharbers.token.AuthTokenTrait
/**
  * Created by yym on 9/14/17.
  */
object RegisterModule extends ModuleTrait with RegisterData {
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_user_register(data: JsValue) => user_register(data)
        case msg_query_register_bd(data: JsValue) => query_bd(data)
//        case msg_pushAdminCommand(data) => pushAdmin(data)(pr)
//        case msg_pushRegisterWithoutCheckCommand(data) => pushRegisterWithoutCheck(data)(pr)
//        case msg_queryRegisterWithIDCommand(data) => queryRegisterWithID(data)(pr)
//        case msg_queryAllRegistersCommand(data) => queryAllRegisters(data)(pr)
//        case msg_deleteRegisterCommand(data) => deleteRegister(data)(pr)
//        case msg_checkRegisterStatusCommand(data) => checkRegisterStatus(data)(pr)
//        case msg_cryptRegisterCodeCommand(data) => cryptRegisterCode(data)(pr)
//        case msg_decryptRegisterCodeCommand(data) => decryptRegisterCode(data)(pr)
//        case msg_checkAuthTokenExpireCommand(data) => decryptRegisterCode(data)(pr)
        case _ => ???
    }
    
    def user_register(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        implicit val db = conn.queryDBInstance("cli").get
        try {
            val id = alEncryptionOpt.md5(UUID.randomUUID().toString)
            val company = (data \ "comapny").asOpt[String].getOrElse(new Exception("info input company name"))
            val linkman = (data \ "linkman").asOpt[String].getOrElse(new Exception("info input linkman name"))
            val email = (data \ "email").asOpt[String].getOrElse(new Exception("info input email"))
            val phone = (data \ "phone").asOpt[String].getOrElse(new Exception("info input phone"))
            val reg_content = Map("company" -> company,
                                  "linkman" -> linkman,
                                  "email" -> email,
                                  "phone" -> phone,
                                  "scope" -> (Nil))
            val register: DBObject = Map("reg_id" -> id,
                                         "reg_content" -> reg_content,
                                         "status" -> 0,
                                         "date" -> new Date().getTime)
            db.insertObject(register, "reg_apply", id)
            (None, Some(toJson(Map("register" -> "ok"))))
        } catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
        
    }
    
    
    
    def query_bd(data: JsValue)(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        implicit val db = conn.queryDBInstance("cli").get
        try {
            val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
            val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(20)
            val map: MongoDBObject = MongoDBObject()
            val result = db.queryMultipleObject(map, "reg_apply", "date", skip, take) { x =>
                val reg_content = x.as[MongoDBObject]("reg_content")
                val email = reg_content.getAs[String]("email").getOrElse("")
                val company = reg_content.getAs[String]("company").getOrElse("")
                val linkman = reg_content.getAs[String]("linkman").getOrElse("")
                val phone = reg_content.getAs[String]("phone").getOrElse("")
                val status = x.getAs[Int]("status").getOrElse(0)
                val date = x.getAs[Number]("date").getOrElse(0).toString.toLong
                Map("email" -> toJson(email),
                    "comapny" -> toJson(company),
                    "linkman" -> toJson(linkman),
                    "phone" -> toJson(phone),
                    "status" -> toJson(status))
            }
            if (result.isEmpty) throw new Exception("unkonwn error")
            else (Some(Map("registers" -> toJson(result))), None)
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def pushAdmin(data : JsValue)
                 (pr : Option[Map[String, JsValue]])
                 (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        
        implicit val dbc = cm.modules.get.get("dbc").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        val db = cm.modules.get.get("dbt").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        try {
            val js = dataMergeWithPr(data, pr)
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
            val res : DBObject = MongoDBObject(
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
            )
            db.insertObject(res, "reg_apply", "reg_id")
            (None, Some(toJson(Map("operation" -> "ok"))))
        }catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
      
    }
    
    def pushRegisterWithoutCheck(data : JsValue)
                 (pr : Option[Map[String, JsValue]])
                 (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        implicit val dbc = cm.modules.get.get("dbc").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        val db = cm.modules.get.get("dbt").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        try {
            val js = dataMergeWithPr(data, pr)
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
        implicit val dbc = cm.modules.get.get("dbc").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        val db = cm.modules.get.get("dbt").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        try {
            val js = dataMergeWithPr(data, pr)
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
        implicit val dbc = cm.modules.get.get("dbc").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        val db = cm.modules.get.get("dbt").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        try {
            val js = dataMergeWithPr(data, pr)
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
        implicit val dbc = cm.modules.get.get("dbc").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        val db = cm.modules.get.get("dbt").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        try {
            val js = dataMergeWithPr(data, pr)
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
        implicit val dbc = cm.modules.get.get("dbc").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        val db = cm.modules.get.get("dbt").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        try {
            val js = dataMergeWithPr(data, pr)
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
