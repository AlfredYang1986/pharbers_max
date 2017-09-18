package bmpattern

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import bmmessages.CommonMessage
import bmutil.errorcode.ErrorCode

import org.apache.log4j.Logger

abstract class msg_LogCommand extends CommonMessage

object LogMessage {
    implicit val common_log : (JsValue, JsValue) => (Option[Map[String, JsValue]], Option[JsValue]) = (ls, data) => {
        try {
            val logger = Logger.getRootLogger

            val user_id = (data \ "user_id").asOpt[String].map (x => x).getOrElse("unknown user")
            val method = (ls \ "method").asOpt[String].map (x => x).getOrElse(throw new Exception("log struct error"))

            logger.info(s"$user_id call $method with args ${data.toString}")
            (Some(Map("status" -> toJson("ok"))), None)

        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }

    case class msg_log(ls : JsValue, data : JsValue)(implicit val l : (JsValue, JsValue) => (Option[Map[String, JsValue]], Option[JsValue])) extends msg_LogCommand
}
