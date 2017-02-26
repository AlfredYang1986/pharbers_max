package com.pharbers.aqll.pattern

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import com.pharbers.aqll.pattern.CommonMessage
import com.pharbers.aqll.util.errorcode.ErrorCode
import org.apache.log4j.Logger
import play.api.mvc.{AnyContent, Request}

abstract class msg_LogCommand extends CommonMessage

object LogMessage {
    implicit val common_log : (JsValue, JsValue, Request[AnyContent]) => (Option[Map[String, JsValue]], Option[JsValue]) = (ls, data, request) => {
        try {
            val logger = Logger.getRootLogger
            val user_id = (data \ "token").asOpt[String].map (x => x).getOrElse((data \ "company").asOpt[String].map (x => x).getOrElse("unknown user"))
            val method = (ls \ "method").asOpt[String].map (x => x).getOrElse(throw new Exception("log struct error"))
            // TODO: 下面这段代码有问题 用户在没有登录的情况下哪里来的 cookie呢，周一修改Bug @李伟
            //logger.info(s"${request} User-Agent: ${request.headers.toMap.get("User-Agent").get.head} Host: ${request.host} User: ${request.cookies.get("user_token").get.value} Company: ${request.cookies.get("company_name_ch").get.value} Method: ${method} Args: ${data.toString()}")
            (Some(Map("status" -> toJson("ok"))), None)
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }

    def writing_log(data : JsValue,method : String, current : Int, totle : Int) {
        val logger = Logger.getLogger("FileExportLog")
        val company = (data \ "company").asOpt[String].map (x => x).getOrElse("unknown company")
        logger.info(s"$company call $method with file export progress [$current/$totle]")
    }

    case class msg_log(ls : JsValue, data : JsValue, request : Request[AnyContent])(implicit val l : (JsValue, JsValue, Request[AnyContent]) => (Option[Map[String, JsValue]], Option[JsValue])) extends msg_LogCommand
}
