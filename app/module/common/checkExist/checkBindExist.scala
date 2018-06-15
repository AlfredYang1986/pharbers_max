package module.common.checkExist

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.bmmessages.CommonModules
import com.pharbers.dbManagerTrait.dbInstanceManager
import play.api.libs.json.JsValue

/**
  * Created by spark on 18-4-20.
  */
trait checkBindExist extends checkExistTrait {

    /**
      * 验证绑定关系的设置前置条件
      */
    val cbeIn: JsValue => DBObject

    /**
      * 验证绑定关系的设置输出条件
      */
    val cbeOut: DBObject => Map[String, JsValue]

    def bindPre(data: JsValue, pr: Option[Map[String, JsValue]], exMsg: String)
               (func: JsValue => DBObject,
                     func_out: DBObject => Map[String, JsValue],
                     coll_name: String)
               (implicit cm: CommonModules): Map[String, JsValue] = {

        val conn = cm.modules.get.get("db").map(x => x.asInstanceOf[dbInstanceManager]).getOrElse(throw new Exception("no db connection"))
        val db = conn.queryDBInstance("client").get

        val obj = db.queryObject(func(data), coll_name)(func_out) match {
            case Some(x) => x
            case None => throw new Exception(exMsg)
        }

        pr.get ++ obj
    }


    /**
      * 验证绑定关系的条件
      */
    val cbe: JsValue => DBObject = jv => DBObject()

    def checkBind(data: JsValue, pr: Option[Map[String, JsValue]], exMsg: String)
                  (func: JsValue => DBObject,
                   func_out: DBObject => Map[String, JsValue],
                   coll_name: String)
                  (implicit cm: CommonModules): Map[String, JsValue] = checkExist(data, pr, exMsg)(func, func_out, coll_name)(cm)

}
