package com.pharbers.aqll.pattern

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import ResultMessage.msg_CommonResultMessage
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

object ResultModule extends ModuleTrait {


	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModule) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case cmd : msg_CommonResultMessage => cmd.func(pr.get)
		case _ => (None, Some(errorToJson("can not parse result")))
		case _ => ???
	}
}