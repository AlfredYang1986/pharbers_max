package com.pharbers.aqll.calc.datacala.algorithm

import com.pharbers.aqll.calc.excel.model.modelRunData

class maxSumData {
    
    def apply(data: Stream[modelRunData]) = {
        lazy val max_filter_data = data.filter(_.ifPanelTouse.equals("1")).sortBy(_.segment.toInt)
        lazy val max_calc_distinct = max_filter_data.map(_.segment).distinct
        max_calc_distinct map { x1 =>
            val max_filter = max_filter_data.filter(x => x.segment.equals(x1))
            (x1, (max_filter.map(_.sumValue).sum, max_filter.map(_.volumeUnit).sum, max_filter.map(_.westMedicineIncome).sum))
        }
    }
}