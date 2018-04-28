package module.common

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.ErrorCode.errorToJson
import module.common.stragety.crud

object processor extends crud {
    def apply(func : JsValue => (Option[Map[String, JsValue]], Option[JsValue]))
             (data : JsValue)(implicit name : String = "function") =
        try {
            func(data)
        } catch {
            case ex : Exception => println(s"$name error=${ex.getMessage}"); (None, Some(errorToJson(ex.getMessage)))
        }

    def returnValue(data : Map[String, JsValue], outter : String = "") : (Option[Map[String, JsValue]], Option[JsValue]) = outter match {
        case "" => (Some(data), None)
        case str : String => (Some(Map(str -> toJson(data))), None)
    }

    def returnValue(data : List[Map[String, JsValue]], outter : String) : (Option[Map[String, JsValue]], Option[JsValue]) =
        (Some(Map(outter -> toJson(data))), None)
}