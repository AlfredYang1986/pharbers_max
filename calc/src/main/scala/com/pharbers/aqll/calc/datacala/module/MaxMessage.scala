package com.pharbers.aqll.calc.datacala.module

import com.pharbers.aqll.calc.datacala.common.BaseArgs
import com.pharbers.aqll.calc.maxmessages.CommonMessage
import com.pharbers.aqll.calc.datacala.common.BaseMaxDataArgs
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.excel.model.modelRunData

object MaxMessage {
    case class msg_IntegratedData(args: BaseArgs) extends CommonMessage
}