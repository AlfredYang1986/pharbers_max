package module.users

import com.mongodb.casbah.Imports._
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait UserCreation {
    // TODO : 这里添加User在数据库中的数据结构
    implicit val d2m : JsValue => DBObject = { js =>
        val data = (js \ "user").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // user_id 唯一标示
        builder += "screen_name" -> (data \ "screen_name").asOpt[String].map (x => x).getOrElse("")
        builder += "screen_photo" -> (data \ "screen_photo").asOpt[String].map (x => x).getOrElse("")
        builder += "email" -> (data \ "email").asOpt[String].map (x => x).getOrElse("")     // 登录的唯一标示
        builder += "phone" -> (data \ "phone").asOpt[String].map (x => x).getOrElse("")

        builder.result
    }

    // TODO : 这里修改User在数据库中的数据结构
    implicit val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "user").asOpt[JsValue].get

        (data \ "screen_name").asOpt[String].map (x => obj += "screen_name" -> x).getOrElse(Unit)
        (data \ "screen_photo").asOpt[String].map (x => obj += "screen_photo" -> x).getOrElse(Unit)
        (data \ "email").asOpt[String].map (x => obj += "email" -> x).getOrElse(Unit)
        (data \ "phone").asOpt[String].map (x => obj += "phone" -> x).getOrElse(Unit)

        obj
    }
}
