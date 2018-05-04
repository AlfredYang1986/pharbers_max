package module.common.checkExist

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports.DBObject

/**
  * Created by spark on 18-4-20.
  */
trait checkAttrExist extends checkExist {
    val ckByCondition: (String, String, JsValue) => DBObject = (by, coll, jv) => DBObject(by -> (jv \ coll \ by).asOpt[String].get)

    val ckAttrExist: JsValue => DBObject
}
