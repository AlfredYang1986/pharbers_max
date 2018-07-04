package module.common.stragety

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.common.datamodel.basemodel
import org.bson.types.ObjectId
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsObject, JsValue}

trait bind[This <: basemodel, That <: basemodel] {
    def bind(data : JsValue) : DBObject
    def unbind(data : JsValue) : DBObject

    def createThis : This
    def createThat : That

    def bindConnection(data : JsValue)
                      (connect : String)
                      (implicit cm : CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        val o = bind(data)
        db.insertObject(o, connect, "_id")
        val tmp = o.get("_id").asInstanceOf[ObjectId].toString

        data.as[JsObject].value.toMap - "condition" ++ Map("bind_id" -> toJson(tmp))
    }

    def unbindConnection(data : JsValue)
                        (connect : String)
                        (implicit cm : CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        val o = unbind(data)
        db.deleteObject(o, connect, "_id")

        val ts = createThis
        val ta = createThat

        Map(
            "unbind " + ts.name + " with " + ta.name -> toJson("success")
        )
    }
}
