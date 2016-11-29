package com.pharbers.aqll.calc.datacala.module

import com.pharbers.aqll.calc.datacala.common.BaseArgs
import com.pharbers.aqll.calc.maxmessages.CommonMessage
import com.pharbers.aqll.calc.datacala.common.BaseMaxDataArgs

object MarketMessage {
    case class msg_IntegratedData(args: BaseArgs) extends CommonMessage
    
    case class msg_MaxData(args: BaseMaxDataArgs) extends CommonMessage
}