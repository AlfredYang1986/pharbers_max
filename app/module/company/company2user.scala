package module.company

import module.users.user
import com.mongodb.casbah
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import module.stragety.{bind, impl, one2many}
import com.mongodb.casbah.Imports.{$or, DBObject, MongoDBObject}

/**
  * Created by spark on 18-4-19.
  */
class company2user extends one2many[company, user] with bind[company, user] {
    override def createThis: company = impl[company]
    override def createThat: user = impl[user]

    override def one2manyssr(obj: Imports.DBObject): Map[String, JsValue] =
        Map("_id" -> toJson(obj.getAs[String]("user_id").get))

    override def one2manyaggregate(lst: List[Map[String, JsValue]]): DBObject =
        $or(lst map (x => DBObject("_id" -> new ObjectId(x("_id").asOpt[String].get))))

    override def one2manysdr(obj: Imports.DBObject): Map[String, JsValue] =
        Map(
            "condition" -> toJson(Map(
                "bind_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
                "user_id" -> toJson(obj.getAs[String]("company_id").get),
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
}
