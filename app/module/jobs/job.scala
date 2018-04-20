package module.jobs

import java.util.Date
import module.jobs.jobStatus._
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.common.datamodel.basemodel

/**
  * Created by spark on 18-4-19.
  */
class job extends basemodel {
    override val name = "job"
    override def runtimeClass: Class[_] = classOf[job]

    val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "job_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    val anqc: JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "job_id").asOpt[String].get
        DBObject("job_id" -> tmp)
    }

    val qcm : JsValue => DBObject = { js =>
        (js \ "condition" \ "jobs").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "job_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "job_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "start_time" -> toJson(obj.getAs[String]("start_time").get),
            "status" -> toJson(obj.getAs[String]("status").get)
        )
    }

    val dr : DBObject => Map[String, JsValue] = sr

    val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop job" -> toJson("success")
        )
    }

    val d2m : JsValue => DBObject = { js =>
        val data = (js \ "job").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // job_id 唯一标示
        builder += "start_time" -> new Date().getTime
        builder += "status" -> jobCreated(data).des

        builder.result
    }

    val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "job").asOpt[JsValue].get

        (data \ "start_time").asOpt[String].map (x => obj += "start_time" -> x).getOrElse(Unit)
        (data \ "status").asOpt[String].map (x => obj += "status" -> x).getOrElse(Unit)

        obj
    }
}