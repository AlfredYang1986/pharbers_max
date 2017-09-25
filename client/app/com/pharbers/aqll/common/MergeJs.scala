package com.pharbers.aqll.common

import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsObject, JsValue}

/**
  * Created by apple on 8/10/17.
*/
object MergeJs {
    
    def dataMergeWithPr(data:JsValue, pr:Option[Map[String, JsValue]]): JsValue = pr match {
        case None => data
        case Some(x) =>
            toJson(x ++ data.as[JsObject].value)
        
    }
}
