package module.jobs

import com.mongodb
import module.users.user
import com.mongodb.casbah
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.common.checkExist.checkBindExist
import module.common.stragety.{bind, impl, one2one}
import com.mongodb.casbah.Imports.{DBObject, MongoDBObject}

/**
  * Created by spark on 18-4-19.
  */
class job2user extends one2one[job, user] with bind[job, user] with checkBindExist {

    override def createThis: job = impl[job]
    override def createThat: user = impl[user]

    override def one2onessr(obj: DBObject): Map[String, JsValue] =
        Map(
            "condition" -> toJson(Map(
                "user_id" -> toJson(obj.getAs[String]("user_id").get)
            ))
        )

    override def unbind(data: JsValue): casbah.Imports.DBObject = {
        val builder = MongoDBObject.newBuilder
        val _id = (data \ "condition" \ "bind_id").asOpt[String].get
        builder += "_id" -> new ObjectId(_id)

        builder.result
    }

    override def bind(data: JsValue): Imports.DBObject = {
        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()
        builder += "job_id" -> (data \ "job" \ "job_id").asOpt[String].get
        builder += "user_id" -> (data \ "user" \ "user_id").asOpt[String].get

        builder.result
    }

    override def one2onesdr(obj: mongodb.casbah.Imports.DBObject): Map[String, JsValue] =
        Map(
            "condition" -> toJson(Map(
                "bind_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
                "job_id" -> toJson(obj.getAs[String]("job_id").get),
                "user_id" -> toJson(obj.getAs[String]("user_id").get)
            ))
        )

    override val checkBindExist: (JsValue) => DBObject = { jv =>
        $and(
            DBObject("job_id" -> (jv \ "job" \ "job_id").asOpt[String].get),
            DBObject("user_id" -> (jv \ "user" \ "user_id").asOpt[String].get)
        )
    }
}