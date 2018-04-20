package module.common.checkExist

import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports.DBObject

/**
  * Created by spark on 18-4-20.
  */
trait checkBindExist extends checkExist {

    /**
      * 验证绑定关系的条件
      */
    val checkBindExist: JsValue => DBObject
}
