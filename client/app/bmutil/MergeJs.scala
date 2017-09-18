package bmutil

import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json.toJson
/**
  * Created by apple on 8/10/17.
  */
object MergeJs {

    def dataMergeWithPr(data:JsValue,pr:Option[Map[String, JsValue]]): JsValue =pr match {
        case None => data
        case Some(x) =>
            toJson(x ++ data.as[JsObject].value)
        
    }
}
