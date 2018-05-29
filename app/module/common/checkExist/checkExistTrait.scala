package module.common.checkExist

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import play.api.libs.json.JsValue

/**
  * Created by spark on 18-4-20.
  */
trait checkExistTrait {
    def checkExist(data: JsValue, pr: Option[Map[String, JsValue]], exMsg: String)
                  (func: JsValue => DBObject,
                   func_out: DBObject => Map[String, JsValue],
                   coll_name: String)
                  (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("cli").get

        db.queryObject(func(data), coll_name)(func_out) match {
            case Some(_) => throw new Exception(exMsg)
            case None => Unit
        }

        pr.get
    }
}