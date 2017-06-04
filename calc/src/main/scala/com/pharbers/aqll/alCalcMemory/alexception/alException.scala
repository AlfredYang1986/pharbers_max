package com.pharbers.aqll.alCalcMemory.alexception

import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import play.api.libs.json.JsValue

/**
  * Created by qianpeng on 2017/6/4.
  */
case class alException(error: JsValue) extends alLoggerMsgTrait {
	logger.error(error.toString())
}
