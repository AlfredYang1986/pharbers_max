package com.pharbers.aqll.calc.datacala.algorithm

import com.pharbers.aqll.calc.excel.model.modelRunData

class maxCalcData {
    
    def apply(data: Stream[modelRunData],avg: Stream[(String, Double, Double)]) = {
        avg foreach { x =>
            data filter (y => y.segment.equals(x._1)) foreach { iter => 
                if (iter.ifPanelAll.equals("1")) {
                    iter.finalResultsValue = iter.sumValue
                    iter.finalResultsUnit = iter.volumeUnit
                }else{
                    iter.finalResultsValue = x._2 * iter.westMedicineIncome * iter.factor.toDouble
                    iter.finalResultsUnit = x._3 * iter.westMedicineIncome * iter.factor.toDouble
                }
            }
        }
        data
    }
}