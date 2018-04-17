package module.stragety

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.datamodel.basemodel
import org.bson.types.ObjectId
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json.toJson

trait one2one[This <: basemodel, That <: basemodel] { this : bind[This, That] =>

    def one2onessr(obj : DBObject) : Map[String, JsValue]           // 一对一中间表返回下一步的直接利用的结果
    def one2onesdr(obj : DBObject) : Map[String, JsValue]           // 详细返回结果

    def queryConnection(data : JsValue, primary_key : String = "_id")
                       (pr : Option[Map[String, JsValue]], outter : String = "")
                       (connect : String)
                       (implicit cm: CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get

        val ts = createThis
        val ta = createThat

        val reVal = db.queryObject(ts.anqc(data), connect)(one2onessr).get
        val result = db.queryObject(ta.qc(toJson(reVal)), ta.names)(ta.dr).get

        val tmp = outter match {
            case "" => ts.name
            case _ => outter
        }

        pr match {
            case None => Map(ta.name -> toJson(result))
            case Some(x) => Map(tmp -> toJson(x.get(tmp).get.as[JsObject].value.toMap ++ Map(ta.name -> toJson(result))))
        }
    }
}
