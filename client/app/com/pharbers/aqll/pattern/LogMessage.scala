package com.pharbers.aqll.pattern

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import org.apache.log4j.Logger
import play.api.mvc.{AnyContent, Request}
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

abstract class msg_LogCommand extends CommonMessage

object LogMessage {
    implicit val common_log : (JsValue, JsValue, Request[AnyContent]) => (Option[Map[String, JsValue]], Option[JsValue]) = (ls, data, request) => {
        try {
            val logger = Logger.getRootLogger
            val user_id = (data \ "token").asOpt[String].map (x => x).getOrElse((data \ "company").asOpt[String].map (x => x).getOrElse("unknown user"))
            val method = (ls \ "method").asOpt[String].map (x => x).getOrElse(throw new Exception("log struct error"))
            var user,company = "Unknown"
            if(request.cookies.toList.size != 0){
                user = request.cookies.get("user_token").get.value
                company = request.cookies.get("company_name_ch").get.value
            }
            logger.info(s"${request} User-Agent: ${request.headers.toMap.get("User-Agent").get.head} Host: ${request.remoteAddress} User: ${user} Company: ${company} Method: ${method} Args: ${data.toString()}")
            (Some(Map("status" -> toJson("ok"))), None)
        } catch {
            case ex : Exception => (None, Some(errorToJson(ex.getMessage)))
        }
    }

    def writing_log(data : JsValue,method : String, current : Int, totle : Int) {
        val logger = Logger.getLogger("FileExportLog")
        val company = (data \ "company").asOpt[String].map (x => x).getOrElse("unknown company")
        logger.info(s"$company call $method with file export progress [$current/$totle]")
    }

    case class msg_log(ls : JsValue, data : JsValue, request : Request[AnyContent])(implicit val l : (JsValue, JsValue, Request[AnyContent]) => (Option[Map[String, JsValue]], Option[JsValue])) extends msg_LogCommand
}
