package module.users.UserData

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait UserResult {
    implicit val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    implicit val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "screen_name" -> toJson(obj.getAs[String]("screen_name").get),
            "screen_photo" -> toJson(obj.getAs[String]("screen_photo").get)
        )
    }

    implicit val dr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "screen_name" -> toJson(obj.getAs[String]("screen_name").get),
            "screen_photo" -> toJson(obj.getAs[String]("screen_photo").get),
            "email" -> toJson(obj.getAs[String]("email").get),
            "phone" -> toJson(obj.getAs[String]("phone").get)
        )
    }

    implicit val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop user" -> toJson("success")
        )
    }
}
