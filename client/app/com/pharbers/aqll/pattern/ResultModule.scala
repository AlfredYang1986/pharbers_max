package com.pharbers.aqll.pattern

import play.api.libs.json.JsValue
import ResultMessage.msg_CommonResultMessage
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.dbmodule.MongoDBModule

object ResultModule extends ModuleTrait {

	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit db: MongoDBModule) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case cmd : msg_CommonResultMessage => cmd.func(pr.get)
		case _ => (None, Some(errorToJson("can not parse result")))
		case _ => ???
	}
}