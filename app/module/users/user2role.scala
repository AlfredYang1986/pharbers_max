package module.users

import com.mongodb.casbah
import com.mongodb.casbah.Imports
import com.mongodb.casbah.Imports.{$or, DBObject, MongoDBObject, _}
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.common.stragety.{bind, impl, one2many}
import module.roles.role
import org.bson.types.ObjectId
import play.api.libs.json.{JsObject, JsValue}
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

    override def queryConnection(data : JsValue, primary_key : String = "_id")
                                (pr : Option[Map[String, JsValue]], outter : String = "")
                                (connect : String)
                                (implicit cm: CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get

        val ts = createThis
        val ta = createThat

        pr match {
            case None => throw new Exception("data not exist")
            case Some(x) =>
                val prMap = x(ts.name).as[JsObject].value.toMap
                if (prMap.isEmpty) throw new Exception("data not exist")

                val tmp = outter match {
                    case "" => ts.name
                    case _ => outter
                }

                val reVal = db.queryMultipleObject(ts.anqc(data), connect)(one2manyssr)
                val result = reVal.size match {
                    case 0 => Nil
                    case _ => db.queryMultipleObject(one2manyaggregate(reVal), ta.names)(ta.sr)
                }

                Map(tmp -> toJson(prMap ++ Map(ta.names -> toJson(result))))
        }
    }
}
