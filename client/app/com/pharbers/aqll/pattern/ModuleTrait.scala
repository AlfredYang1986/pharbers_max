package com.pharbers.aqll.pattern

import play.api.libs.json.JsValue

trait ModuleTrait {
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue])
}