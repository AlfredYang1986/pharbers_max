package module.common

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.ErrorCode.errorToJson
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager

object processor {
    def apply(func : JsValue => (Option[Map[String, JsValue]], Option[JsValue]))
             (data : JsValue)(implicit name : String = "function") =
        try {
            func(data)
        } catch {
            case ex : Exception => println(s"$name error=${ex.getMessage}"); (None, Some(errorToJson(ex.getMessage)))
        }

    def creation(data : JsValue, primary_key : String = "_id")(db_name : String)
                (implicit func : JsValue => DBObject,
                 func_out : DBObject => Map[String, JsValue],
                 cm: CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get

        val o = func(data)
        db.insertObject(o, db_name, primary_key)
        func_out(o)
    }

    def remove(data : JsValue, primary_key : String = "_id")(db_name : String)
              (implicit func : JsValue => DBObject,
               func_out : DBObject => Map[String, JsValue],
               cm: CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get

        val o = func(data)
        db.deleteObject(o, db_name, primary_key)
        func_out(o)
    }

    def query(data : JsValue)(db_name : String)
             (implicit func : JsValue => DBObject,
              func_out : DBObject => Map[String, JsValue],
              cm: CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        db.queryObject(func(data), db_name)(func_out).get
    }

    def queryMulti(data : JsValue)(db_name : String)
                  (implicit func : JsValue => DBObject,
                   func_out : DBObject => Map[String, JsValue],
                   cm: CommonModules) : List[Map[String, JsValue]] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(20)
        val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
        val sort = (data \ "sort").asOpt[String].map (x => x).getOrElse("date")
        db.queryMultipleObject(func(data), db_name, sort, skip, take)(func_out)
    }

    def update(data : JsValue)(db_name : String)
              (implicit func : JsValue => DBObject,
               func_update : (DBObject, JsValue) => DBObject,
               func_out : DBObject => Map[String, JsValue],
               cm: CommonModules) : List[Map[String, JsValue]] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get
        val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(20)
        val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
        val sort = (data \ "sort").asOpt[String].map (x => x).getOrElse("date")
        db.queryMultipleObject(func(data), db_name, sort, skip, take)(func_out)
    }

    def returnValue(data : Map[String, JsValue], outter : String = "") : (Option[Map[String, JsValue]], Option[JsValue]) = outter match {
        case "" => (Some(data), None)
        case str : String => (Some(Map(str -> toJson(data))), None)
    }

    def returnValue(data : List[Map[String, JsValue]], outter : String) : (Option[Map[String, JsValue]], Option[JsValue]) =
        (Some(Map(outter -> toJson(data))), None)
}
