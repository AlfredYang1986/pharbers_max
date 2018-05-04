package module.users

import com.mongodb
import com.mongodb.casbah
import module.company.company
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
class user2company extends one2one[user, company] with bind[user, company] with checkBindExist {
    override def createThis: user = impl[user]
    override def createThat: company = impl[company]

    override def one2onessr(obj: DBObject): Map[String, JsValue] =
        Map(
            "condition" -> toJson(Map(
                "company_id" -> toJson(obj.getAs[String]("company_id").get)
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
        builder += "user_id" -> (data \ "user" \ "user_id").asOpt[String].get
        builder += "company_id" -> (data \ "company" \ "company_id").asOpt[String].get

        builder.result
    }

    override def one2onesdr(obj: mongodb.casbah.Imports.DBObject): Map[String, JsValue] =
        Map(
            "condition" -> toJson(Map(
                "bind_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
                "user_id" -> toJson(obj.getAs[String]("user_id").get),
                "company_id" -> toJson(obj.getAs[String]("company_id").get)
            ))
        )

    override val checkBindExist: JsValue => DBObject = { jv =>
        $and(
            DBObject("user_id" -> (jv \ "user" \ "user_id").asOpt[String].get),
            DBObject("company_id" -> (jv \ "company" \ "company_id").asOpt[String].get)
        )
    }
}