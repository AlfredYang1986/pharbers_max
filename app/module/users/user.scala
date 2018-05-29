package module.users

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports._
import module.common.datamodel.basemodel
import module.common.checkExist.checkAttrExist

/**
  * Created by spark on 18-4-19.
  */
class user extends basemodel with checkAttrExist with authTrait {
    override val name = "user"
    override def runtimeClass: Class[_] = classOf[user]

    override val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "user_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    override val anqc: JsValue => DBObject = { js =>
        val tmp = (js \ "user" \ "user_id").asOpt[String].get
        DBObject("user_id" -> tmp)
    }

    override val qcm : JsValue => DBObject = { js =>
        (js \ "condition" \ "users").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    override val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    override val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "screen_name" -> toJson(obj.getAs[String]("screen_name").get),
            "screen_photo" -> toJson(obj.getAs[String]("screen_photo").get)
        )
    }

    override val dr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "screen_name" -> toJson(obj.getAs[String]("screen_name").get),
            "screen_photo" -> toJson(obj.getAs[String]("screen_photo").get),
            "email" -> toJson(obj.getAs[String]("email").get),
//            "password" -> toJson(obj.getAs[String]("password").get),
            "phone" -> toJson(obj.getAs[String]("phone").get)
        )
    }

    override val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop user" -> toJson("success")
        )
    }

    override val d2m : JsValue => DBObject = { js =>
        val data = (js \ "user").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // user_id 唯一标示
        builder += "screen_name" -> (data \ "screen_name").asOpt[String].map (x => x).getOrElse("")
        builder += "screen_photo" -> (data \ "screen_photo").asOpt[String].map (x => x).getOrElse("")
        builder += "email" -> (data \ "email").asOpt[String].map (x => x).getOrElse("")     // 登录的唯一标示
        builder += "password" -> (data \ "password").asOpt[String].map (x => x).getOrElse("")
        builder += "phone" -> (data \ "phone").asOpt[String].map (x => x).getOrElse("")

        builder.result
    }

    override val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "user").asOpt[JsValue].get

        (data \ "screen_name").asOpt[String].map (x => obj += "screen_name" -> x).getOrElse(Unit)
        (data \ "screen_photo").asOpt[String].map (x => obj += "screen_photo" -> x).getOrElse(Unit)
        (data \ "email").asOpt[String].map (x => obj += "email" -> x).getOrElse(Unit)
        (data \ "password").asOpt[String].map (x => obj += "password" -> x).getOrElse(Unit)
        (data \ "phone").asOpt[String].map (x => obj += "phone" -> x).getOrElse(Unit)

        obj
    }

    override val ckAttrExist: JsValue => DBObject = ckByCondition("email", "user", _)
}