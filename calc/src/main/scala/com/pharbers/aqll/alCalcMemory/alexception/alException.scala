package com.pharbers.aqll.alCalcMemory.alexception

import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import play.api.libs.json.JsValue

/**
  * Created by qianpeng on 2017/6/4.
  */
case class alException(error: JsValue) extends alLoggerMsgTrait {
	logger.error(error.toString())
}

// modify by clock on 17/6/5
// 使用伴生对象，处理String参数异常
//object alException extends alLoggerMsgTrait  {
//    def apply(errorStr: String) = logger.error(errorStr)
//}
