package com.pharbers.common

import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json.toJson

object MergeStepResult {
	def apply(data : JsValue, pr : Option[Map[String, JsValue]]) : JsValue = pr match {
		case None => data
		case Some(x) => toJson(data.as[JsObject].value.toMap ++ x)
	}
}