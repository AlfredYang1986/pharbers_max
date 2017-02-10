package com.pharbers.aqll.calc.datacala.module

import com.pharbers.aqll.calc.datacala.common.BaseArgs
import com.pharbers.aqll.calc.adapter.Adapter

class IntegratedData(adapter: Adapter, data: BaseArgs) {
    lazy val integrateData = adapter.integrateddata(data)
}