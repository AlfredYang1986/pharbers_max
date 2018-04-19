package module.users

import org.bson.types.ObjectId
import module.datamodel.basemodel
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports._
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.common.MergeStepResult
import module.common.pharbersmacro.CURDMacro.queryMacro

/**
  * Created by spark on 18-4-19.
  */
class user extends basemodel with checkExist {
    override val name = "user"
    override def runtimeClass: Class[_] = classOf[user]

    val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "user_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    val anqc: JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "user_id").asOpt[String].get
        DBObject("user_id" -> tmp)
    }

    val qcm : JsValue => DBObject = { js =>
        (js \ "condition" \ "users").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "screen_name" -> toJson(obj.getAs[String]("screen_name").get),
            "screen_photo" -> toJson(obj.getAs[String]("screen_photo").get)
        )
    }

    val dr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "user_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "screen_name" -> toJson(obj.getAs[String]("screen_name").get),
            "screen_photo" -> toJson(obj.getAs[String]("screen_photo").get),
            "email" -> toJson(obj.getAs[String]("email").get),
            "phone" -> toJson(obj.getAs[String]("phone").get)
        )
    }

    val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop user" -> toJson("success")
        )
    }

    val d2m : JsValue => DBObject = { js =>
        val data = (js \ "user").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // user_id 唯一标示
        builder += "screen_name" -> (data \ "screen_name").asOpt[String].map (x => x).getOrElse("")
        builder += "screen_photo" -> (data \ "screen_photo").asOpt[String].map (x => x).getOrElse("")
        builder += "email" -> (data \ "email").asOpt[String].map (x => x).getOrElse("")     // 登录的唯一标示
        builder += "phone" -> (data \ "phone").asOpt[String].map (x => x).getOrElse("")

        builder.result
    }

    val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "user").asOpt[JsValue].get

        (data \ "screen_name").asOpt[String].map (x => obj += "screen_name" -> x).getOrElse(Unit)
        (data \ "screen_photo").asOpt[String].map (x => obj += "screen_photo" -> x).getOrElse(Unit)
        (data \ "email").asOpt[String].map (x => obj += "email" -> x).getOrElse(Unit)
        (data \ "phone").asOpt[String].map (x => obj += "phone" -> x).getOrElse(Unit)

        obj
    }
}

trait checkExist {

    val ckByEmail: JsValue => DBObject = { js =>
        val tmp = (js \ "user" \ "email").asOpt[String].get
        DBObject("email" -> tmp)
    }

    val ckByName: JsValue => DBObject = { js =>
        val tmp = (js \ "user" \ "name").asOpt[String].get
        DBObject("name" -> tmp)
    }

    def verifyRegister(data: JsValue, pr: Option[Map[String, JsValue]])
                      (func : JsValue => DBObject,
                       func_out : DBObject => Map[String, JsValue],
                       coll_name: String)
                      (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get

        db.queryObject(func(data), coll_name)(func_out) match {
            case Some(_) => throw new Exception("user is repeat")
            case None => Map.empty
        }
    }
}