package module.datamodel

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue

trait basemodel {

    val primary_id = "_id"
    val name : String
    val names : String = name + "s"

    /**
      * query condition : 查询函数
      */
    val qc : JsValue => DBObject

    /**
      * query condition multi : 多重查询函数
      */
    val qcm : JsValue => DBObject

    /**
      * simple simple result : 极简返回结果
      */
    val ssr : DBObject => Map[String, JsValue]

    /**
      * simple result : 简单返回结果
      */
    val sr : DBObject => Map[String, JsValue]

    /**
      * detail result : 详细放回结果
      */
    val dr : DBObject => Map[String, JsValue]

    /**
      * pop result ： 弹出返回结果
      */
    val popr : DBObject => Map[String, JsValue]

    /**
      * d2m : 创建
      */
    val d2m : JsValue => DBObject

    /**
      * up2m : 修改接口
      */
    val up2m : (DBObject, JsValue) => DBObject
}
