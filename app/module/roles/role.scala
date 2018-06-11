package module.roles

import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import module.common.checkExist.checkAttrExist
import play.api.libs.json.Json.toJson
import module.common.datamodel.basemodel

/**
  * Created by clock on 18-6-11.
  */
class role extends basemodel with checkAttrExist {
    override val name = "role"
    override def runtimeClass: Class[_] = classOf[role]

    val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "role_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    val anqc: JsValue => DBObject = { js =>
        val tmp = (js \ "role" \ "role_id").asOpt[String].get
        DBObject("role_id" -> tmp)
    }

    val qcm : JsValue => DBObject = { js =>
        (js \ "condition" \ "roles").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "role_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "role_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "role_name" -> toJson(obj.getAs[String]("role_name").get),
            "role_des" -> toJson(obj.getAs[String]("role_des").get)
        )
    }

    val dr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "role_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "role_name" -> toJson(obj.getAs[String]("role_name").get),
            "role_des" -> toJson(obj.getAs[String]("role_des").get)
        )
    }

    val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop role" -> toJson("success")
        )
    }

    val d2m : JsValue => DBObject = { js =>
        val data = (js \ "role").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // role_id 唯一标示
        builder += "role_name" -> (data \ "role_name").asOpt[String].map (x => x).getOrElse("")
        builder += "role_des" -> (data \ "role_des").asOpt[String].map (x => x).getOrElse("")

        builder.result
    }

    val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "role").asOpt[JsValue].get

        (data \ "role_name").asOpt[String].map (x => obj += "role_name" -> x).getOrElse(Unit)
        (data \ "role_des").asOpt[String].map (x => obj += "role_des" -> x).getOrElse(Unit)

        obj
    }

    override val ckAttrExist: JsValue => DBObject = ckByCondition("role_name", "role", _)

    val qrc : JsValue => DBObject = { js =>
        val tmp = (js \ "role" \ "role_name").asOpt[String].get
        DBObject("role_name" -> tmp)
    }
}
