package com.pharbers.aqll.calc.split

import com.pharbers.aqll.calc.datacala.common.BaseArgs
import com.pharbers.aqll.calc.adapter.Adapter

class splitdata(adapter: Adapter, data: BaseArgs) {
    lazy val d = adapter.splitdata(data)
}

//class splitproduct(adapter: Adapter, data: BaseArgs) {
//    lazy val productigad = adapter.splitproductdata(data)
//}

