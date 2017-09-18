package bmlogic.register

import bmlogic.register.content.ContentData
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
/**
  * Created by yym on 9/13/17.
  */
trait RegisterData extends ContentData{
    implicit val m2d : JsValue => DBObject = { js =>
        val build = MongoDBObject.newBuilder
        build += "reg_id" -> (js \ "reg_id").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
        build += "date" ->(js \ "date") .asOpt[String].map(x =>x).getOrElse(throw  new Exception("input error"))
        build += "status" -> (js \ "status").asOpt[String].map(x =>x).getOrElse(throw new Exception("input error"))
        
        val content = MongoDBObject.newBuilder
        content  += "email" -> pushEmail(js)
        content += "secret" -> pushSecret(js)
        content += "name" -> pushName(js)
        content += "phone" -> pushPhone(js)
        content += "scope" -> pushScope(js)
        content += "other" -> pushOther(js)
        
        build += "reg_content" -> content.result
        
        build.result
    }
    
    implicit val d2m : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "reg_id" -> toJson(obj.getAs[String]("reg_id").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "reg_content" -> toJson(Map("email" -> queryEmailContent(obj),
                "secret" -> querySecretContent(obj),
                "name" -> queryNameContent(obj),
                "phone" -> queryPhoneContent(obj),
                "scope" -> queryScopeContent(obj),
                "other" -> queryOtherContent(obj))),
            "date" -> toJson(obj.getAs[String]("date").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "status" -> toJson(obj.getAs[String]("date").map (x => x).getOrElse(throw new Exception("db prase error")))
            
        )
    }
}
