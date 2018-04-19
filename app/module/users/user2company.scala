package module.users

import com.mongodb
import com.mongodb.casbah
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import module.stragety.{bind, impl, one2one}
import com.mongodb.casbah.Imports.{DBObject, MongoDBObject}
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.company.company

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
                "user_id" -> toJson(obj.getAs[String]("company_id").get),
                "company_id" -> toJson(obj.getAs[String]("company_id").get)
            ))
        )
}

trait checkBindExist {

    val vbc: JsValue => DBObject = { jv =>
        $and(
            DBObject("user_id" -> (jv \ "user" \ "user_id").asOpt[String].get),
            DBObject("company_id" -> (jv \ "company" \ "company_id").asOpt[String].get)
        )
    }

    def verifyBind(data: JsValue,
                   func: JsValue => DBObject,
                   func_out: DBObject => Map[String, JsValue])
                  (coll_name: String)
                  (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get

        db.queryObject(func(data), coll_name)(func_out) match {
            case Some(_) => throw new Exception("user and company bind has been use")
            case None => Map.empty
        }
    }
}