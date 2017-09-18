package bmlogic.users.UserData

import bmlogic.register.content.ContentData
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by yym on 9/15/17.
  */
trait UserData extends ContentData{
    implicit val m2d : JsValue => DBObject = { js =>
        val build = MongoDBObject.newBuilder
        build += "user_id" -> (js \ "user_id").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
        build += "date" ->(js \ "date") .asOpt[String].map(x =>x).getOrElse(throw  new Exception("input error"))
        
        val profile = MongoDBObject.newBuilder
        profile  += "email" -> pushEmail(js)
        profile += "secret" -> pushSecret(js)
        profile += "name" -> pushName(js)
        profile += "phone" -> pushPhone(js)
        profile += "scope" -> pushScope(js)
        profile += "other" -> pushOther(js)
        
        build += "profile" -> profile.result
        
        build.result
    }
    
    implicit val d2m : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "reg_id" -> toJson(obj.getAs[String]("reg_id").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "profile" -> toJson(Map("email" -> queryEmailContent(obj),
                "secret" -> querySecretContent(obj),
                "name" -> queryNameContent(obj),
                "phone" -> queryPhoneContent(obj),
                "scope" -> queryScopeContent(obj),
                "other" -> queryOtherContent(obj))),
            "date" -> toJson(obj.getAs[String]("date").map (x => x).getOrElse(throw new Exception("db prase error")))
        
        )
    }
}
