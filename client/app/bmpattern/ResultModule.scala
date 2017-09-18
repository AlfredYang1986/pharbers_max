package bmpattern

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import bmmessages.MessageDefines
import bmmessages.CommonModules
import ResultMessage.msg_CommonResultMessage

import bmutil.errorcode.ErrorCode

object ResultModule extends ModuleTrait {
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case cmd : msg_CommonResultMessage => cmd.func(pr.get)
		case _ => (None, Some(ErrorCode.errorToJson("can not parse result")))
	}
}