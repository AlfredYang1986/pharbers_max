package module.company

import org.bson.types.ObjectId
import module.datamodel.basemodel
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import com.mongodb.casbah.Imports.{$or, DBObject, MongoDBObject}

/**
  * Created by spark on 18-4-19.
  */
class company extends basemodel with checkCompanyExist {
    override val name: String = "company"
    override lazy val names: String = "companies"
    override def runtimeClass: Class[_] = classOf[company]

    val qc : JsValue => DBObject = { js =>
        val tmp = (js \ "condition" \ "company_id").asOpt[String].get
        DBObject("_id" -> new ObjectId(tmp))
    }

    val anqc: JsValue => _root_.com.mongodb.casbah.Imports.DBObject = { js =>
        val tmp = (js \ "condition" \ "company_id").asOpt[String].get
        DBObject("company_id" -> tmp)
    }

    val qcm : JsValue => DBObject = { js =>
        (js \ "condition" \ "companies").asOpt[List[String]].get match {
            case Nil => DBObject("query" -> "none")
            case ll : List[String] => $or(ll map (x => DBObject("_id" -> new ObjectId(x))))
        }
    }

    val ssr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "company_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString)
        )
    }

    val sr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "company_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "company_name" -> toJson(obj.getAs[String]("company_name").get),
            "company_des" -> toJson(obj.getAs[String]("company_des").get)
        )
    }

    val dr : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "company_id" -> toJson(obj.getAs[ObjectId]("_id").get.toString),
            "company_name" -> toJson(obj.getAs[String]("company_name").get),
            "company_des" -> toJson(obj.getAs[String]("company_des").get)
        )
    }

    val popr : DBObject => Map[String, JsValue] = { _ =>
        Map(
            "pop company" -> toJson("success")
        )
    }

    val d2m : JsValue => DBObject = { js =>
        val data = (js \ "company").asOpt[JsValue].map (x => x).getOrElse(toJson(""))

        val builder = MongoDBObject.newBuilder
        builder += "_id" -> ObjectId.get()      // user_id 唯一标示
        builder += "company_name" -> (data \ "company_name").asOpt[String].map (x => x).getOrElse("")
        builder += "company_des" -> (data \ "company_des").asOpt[String].map (x => x).getOrElse("")

        builder.result
    }

    val up2m : (DBObject, JsValue) => DBObject = { (obj, js) =>
        val data = (js \ "user").asOpt[JsValue].get

        (data \ "company_name").asOpt[String].map (x => obj += "company_name" -> x).getOrElse(Unit)
        (data \ "company_des").asOpt[String].map (x => obj += "company_des" -> x).getOrElse(Unit)

        obj
    }
}

trait checkCompanyExist {

    val ckBy: (String, JsValue) => (String, Any) = (by, jv) => by -> (jv \ "company" \ by).asOpt[String].get
    val ckByName: (JsValue) => (String, Any) = ckBy("company_name", _)

    def ckByCondition(jv: JsValue)(func: JsValue =>(String, Any)) : DBObject = DBObject(func(jv))

    def verifyRegister(data: JsValue, pr: Option[Map[String, JsValue]])
                      (func: JsValue => (String, Any),
                       func_out: DBObject => Map[String, JsValue],
                       coll_name: String)
                      (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get

        db.queryObject(ckByCondition(data)(func), coll_name)(func_out) match {
            case Some(_) => throw new Exception("company name has been use")
            case None => Map.empty
        }
    }
}