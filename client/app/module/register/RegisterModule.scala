package module.register

import java.util.{Date, UUID}

import com.mongodb.DBObject
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import bmlogic.register.RegisterMessage._
import com.pharbers.ErrorCode
import com.pharbers.aqll.common.DBConection
import com.pharbers.aqll.common.MaxEnmeration.{alRegisterStatus, alUserScope}
import com.pharbers.aqll.common.MergeJs._
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.sercurity.Sercurity
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.cliTraits.DBTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.pharbers.mongodbConnect.from
import com.pharbers.token.AuthTokenTrait

object RegisterModule extends ModuleTrait {
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_user_register(data: JsValue) => user_register(data)
        case msg_query_register_bd(data: JsValue) => query_bd(data)
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
                                         "status" -> alRegisterStatus.posted.id,
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
}
