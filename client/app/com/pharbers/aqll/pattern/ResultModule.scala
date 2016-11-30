package com.pharbers.aqll.pattern

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import ResultMessage.msg_CommonResultMessage

object ResultModule extends ModuleTrait {
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case cmd : msg_CommonResultMessage => cmd.func(pr.get)
//		case _ => (None, Some(ErrorCode.errorToJson("can not parse result")))
		case _ => ???
	}
}