package bmpattern

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import bmmessages.CommonMessage

import bmutil.errorcode.ErrorCode

abstract class msg_ResultCommand extends CommonMessage

object ResultMessage {
	implicit val common_result : Map[String, JsValue] => (Option[Map[String, JsValue]], Option[JsValue]) = m => (Some(Map("status" -> toJson("ok"), "result" -> toJson(m))), None)
	implicit val lst_result : Map[String, JsValue] => (Option[Map[String, JsValue]], Option[JsValue]) = m => (Some(m + ("status" -> toJson("ok"))), None)

	case class msg_CommonResultMessage()(implicit val func : Map[String, JsValue] => (Option[Map[String, JsValue]], Option[JsValue])) extends msg_ResultCommand
}
