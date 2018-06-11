package module.users

import com.mongodb.casbah
import com.mongodb.casbah.Imports
import com.mongodb.casbah.Imports.{$or, DBObject, MongoDBObject, _}
import module.common.stragety.{bind, impl, one2many}
import module.roles.role
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by spark on 18-4-19.
  */
class user2role extends one2many[user, role] with bind[user, role] {
    override def createThis: user = impl[user]
    override def createThat: role = impl[role]

    override def one2manyssr(obj: Imports.DBObject): Map[String, JsValue] =
        Map("_id" -> toJson(obj.getAs[String]("role_id").get))

    override def one2manyaggregate(lst: List[Map[String, JsValue]]): DBObject =
        $or(lst map (x => DBObject("_id" -> new ObjectId(x("_id").asOpt[String].get))))

    override def one2manysdr(obj: Imports.DBObject): Map[String, JsValue] =
        Map(
            "condition" -> toJson(Map(
                "bind_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
                "role_id" -> toJson(obj.getAs[String]("role_id").get),
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
        builder += "role_id" -> (data \ "role" \ "role_id").asOpt[String].get
        builder += "user_id" -> (data \ "user" \ "user_id").asOpt[String].get

        builder.result
    }
}
