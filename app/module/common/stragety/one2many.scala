package module.common.stragety

import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports.DBObject
import play.api.libs.json.{JsObject, JsValue}

import module.common.datamodel.basemodel
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager

trait one2many[This <: basemodel, That <: basemodel] { this : bind[This, That] =>

    def one2manyssr(obj : DBObject) : Map[String, JsValue]                                  // 中间表返回下一步的直接利用的结果
    def one2manysdr(obj : DBObject) : Map[String, JsValue]                                  // 详细返回结果
    def one2manyaggregate(lst : List[Map[String, JsValue]]) : DBObject                      // 中间表返回下一步的直接利用的结果

    def queryConnection(data : JsValue, primary_key : String = "_id")
                       (pr : Option[Map[String, JsValue]], outter : String = "")
                       (connect : String)
                       (implicit cm: CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get

        val ts = createThis
        val ta = createThat

        val reVal = db.queryMultipleObject(ts.anqc(data), connect)(one2manyssr)
        val result = db.queryMultipleObject(one2manyaggregate(reVal), ta.names)(ta.dr)

        val tmp = outter match {
            case "" => ts.name
            case _ => outter
        }

        pr match {
            case None => Map(ta.name -> toJson(result))
            case Some(x) => Map(tmp -> toJson(x(tmp).as[JsObject].value.toMap ++ Map(ta.names -> toJson(result))))
        }
    }
}
