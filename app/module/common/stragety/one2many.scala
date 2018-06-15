package module.common.stragety

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.common.datamodel.basemodel
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsObject, JsValue}

trait one2many[This <: basemodel, That <: basemodel] { this : bind[This, That] =>

    def one2manyssr(obj : DBObject) : Map[String, JsValue]                                  // 中间表返回下一步的直接利用的结果
    def one2manysdr(obj : DBObject) : Map[String, JsValue]                                  // 详细返回结果
    def one2manyaggregate(lst : List[Map[String, JsValue]]) : DBObject                      // 中间表返回下一步的直接利用的结果

    def queryConnection(data : JsValue, primary_key : String = "_id")
                       (pr : Option[Map[String, JsValue]], outter : String = "")
                       (connect : String)
                       (implicit cm: CommonModules) : Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

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
                    case _ => db.queryMultipleObject(one2manyaggregate(reVal), ta.names)(ta.dr)
                }

                Map(tmp -> toJson(prMap ++ Map(ta.names -> toJson(result))))
        }
    }
}
