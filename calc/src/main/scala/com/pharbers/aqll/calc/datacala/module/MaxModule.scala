package com.pharbers.aqll.calc.datacala.module

import com.pharbers.aqll.calc.datacala.common._
import com.pharbers.aqll.calc.datacala.algorithm.maxUnionAlgorithm
import com.pharbers.aqll.calc.datacala.module.MaxMessage._
import com.pharbers.aqll.calc.maxmessages.MaxMessageTrait
import com.pharbers.aqll.calc.util.StringOption
import com.pharbers.aqll.calc.adapter.Adapter
import com.pharbers.aqll.calc.adapter.SplitAdapter
import com.pharbers.aqll.calc.adapter.MaxUnionAdapter

object MaxModule {
    def dispatchMessage(dataMsg: MaxMessageTrait): Option[DataIOTrait] = dataMsg match{
        case msg_IntegratedData(data) => new IntegratedData(new MaxUnionAdapter(), data).integrateData
        case _ => ???
    }
}

