package module.common

import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json.toJson

object MergeStepResult {
	def apply(data : JsValue, pr : Option[Map[String, JsValue]]) : JsValue = pr match {
		case None => data
		case Some(x) =>
//			var tmp = data.as[JsObject].value.toMap
//			x.map { y =>
//				tmp.get(y._1) match {
//					case Some(z) => ???
//					case None => tmp += y
//				}
//			}


			toJson(data.as[JsObject].value.toMap ++ x)
	}


}