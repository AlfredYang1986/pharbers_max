package com.pharbers.aqll.calc.datacala.module

import com.pharbers.aqll.calc.datacala.common.BaseMaxDataArgs
import com.pharbers.aqll.calc.adapter.Adapter
import scala.collection.mutable.ArrayBuffer
import com.pharbers.aqll.calc.excel.model.modelRunData

class DataMaxNew(adapter: Adapter, data: BaseMaxDataArgs) {
    lazy val data_max_new = adapter.maxdata(data)
}