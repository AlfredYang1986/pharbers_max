package module.register

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.ErrorCode
import com.pharbers.bmmessages.{CommonModules, MessageDefines}
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.register.RegisterData._
import module.register.RegisterMessage._

object RegisterModule extends ModuleTrait with RegisterData {
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_user_register(data: JsValue) => user_register(data)
        case msg_query_register_bd(data: JsValue) => query_bd(data)
        case msg_is_user_register(data : JsValue) => user_is_register(data)
        case msg_approve_reg(data : JsValue) => approve_reg(data)(pr)
        case _ => throw new Exception("function is not impl")
    }

    def user_is_register(data : JsValue)(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o : DBObject = conditions(data)
            db.queryObject(o, "reg_apply")(detail2map).map { x =>
                (Some(Map("apply" -> toJson(x))), None)

            }.getOrElse(throw new Exception(""))

        } catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def user_register(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val o: DBObject = m2d(data)
            db.insertObject(o, "reg_apply", "reg_id")
            (Some(Map("registers" -> toJson("ok"))), None)
        } catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
        
    }
    
    def query_bd(data: JsValue)(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        try {
            val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
            val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(20)
            val o = conditions(data)
           db.queryMultipleObject(o, "reg_apply", "date", skip, take) match {
               case Nil => throw new Exception("unkonwn error")
               case lst => (Some(Map("registers" -> toJson(lst))), None)
           }
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }

    def approve_reg(data : JsValue)
                   (pr : Option[Map[String, JsValue]])
                   (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {

        try {
            val conn = cm.modules.get.get("db").map (x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
            val db = conn.queryDBInstance("cli").get

            val app = pr.get.get("apply").get
            val token = pr.get.get("token").get

            val email = (app \ "email").asOpt[String].get
            db.queryObject(DBObject("reg_content.email" -> email), "reg_apply") { x =>
                x += "status" -> 1.asInstanceOf[Number]
                db.updateObject(x, "reg_apply", "reg_id")
                x
            }

            (Some(Map("token" -> toJson(token))), None)

        } catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
}
