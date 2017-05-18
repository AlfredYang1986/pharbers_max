package com.pharbers.aqll.pattern

import com.pharbers.aqll.common.alErrorCode.alErrorCode
import play.api.libs.json.JsValue
import com.pharbers.aqll.pattern.LogMessage._

object LogModule extends ModuleTrait {
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case cmd : msg_log => cmd.l(cmd.ls, cmd.data, cmd.request)
        case _ => (None, Some(alErrorCode.errorToJson("can not parse result")))
    }
}
