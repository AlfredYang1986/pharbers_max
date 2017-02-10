package com.pharbers.aqll.calc.datacala.algorithm

import com.pharbers.aqll.calc.excel.model.modelRunData



object maxCalcAlgorithm {
    def apply(data : Stream[modelRunData]) = {
        val data_calc = data.toStream
        lazy val max_filter_data = data_calc.filter(_.ifPanelTouse.equals("1")).sortBy(_.segment.toInt)
        lazy val max_calc_distinct = max_filter_data.map(_.segment).distinct
        val sum_data = max_calc_distinct map { x1 =>
            val max_filter = max_filter_data.filter(x => x.segment.equals(x1))
            (x1, (max_filter.map(_.sumValue).sum, max_filter.map(_.volumeUnit).sum, max_filter.map(_.westMedicineIncome).sum))
        }
        sum_data.foreach { x1 =>
            data filter (x2 => x2.segment.equals(x1._1)) foreach { iter =>
                if (iter.ifPanelAll.equals("1")) {
                    iter.finalResultsValue = iter.sumValue
                    iter.finalResultsUnit = iter.volumeUnit
                } else {
                    iter.finalResultsValue = x1._2._1 / x1._2._3 * iter.westMedicineIncome * iter.factor.toDouble
                    iter.finalResultsUnit = x1._2._2 / x1._2._3 * iter.westMedicineIncome * iter.factor.toDouble
                }
            }
        } 
    }
}