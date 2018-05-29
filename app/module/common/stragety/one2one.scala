package module.common.stragety

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import module.common.datamodel.basemodel
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsObject, JsValue}

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

        pr match {
            case None => throw new Exception("data not exist")
            case Some(x) =>
                val prMap = x(ts.name).as[JsObject].value.toMap
                if(prMap.isEmpty) throw new Exception("data not exist")

                val tmp = outter match {
                    case "" => ts.name
                    case _ => outter
                }

                val reVal = db.queryObject(ts.anqc(data), connect)(one2onessr)
                val result = reVal match {
                    case None => Map().empty
                    case Some(_) => db.queryObject(ta.qc(toJson(reVal)), ta.names)(ta.dr).get
                }

                Map(tmp -> toJson(prMap ++ Map(ta.name -> toJson(result.map(x => x._1.toString -> x._2)))))
        }
    }
}
