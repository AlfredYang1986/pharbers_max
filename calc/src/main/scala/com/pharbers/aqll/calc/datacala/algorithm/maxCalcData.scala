package com.pharbers.aqll.calc.datacala.algorithm

import com.pharbers.aqll.calc.excel.model.modelRunData

class maxCalcData {
    
    def apply(data: Stream[modelRunData],avg1: Double, avg2: Double) = {
        data foreach { iter => 
            if (iter.ifPanelAll.equals("1")) {
                iter.finalResultsValue = iter.sumValue
                iter.finalResultsUnit = iter.volumeUnit
            }else{
                iter.finalResultsValue = avg1 * iter.westMedicineIncome * iter.factor.toDouble
                iter.finalResultsUnit = avg2 * iter.westMedicineIncome * iter.factor.toDouble
            }
        }
        data
    }
}