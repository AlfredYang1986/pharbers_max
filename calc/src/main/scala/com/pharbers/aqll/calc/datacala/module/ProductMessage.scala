package com.pharbers.aqll.calc.datacala.module

import com.pharbers.aqll.calc.datacala.common.BaseArgs
import com.pharbers.aqll.calc.maxmessages.CommonMessage

object ProductMessage {
    case class msg_IntegratedData(val args : BaseArgs) extends CommonMessage 
}