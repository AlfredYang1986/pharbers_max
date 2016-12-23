package com.pharbers.aqll.calc.split

import com.pharbers.aqll.calc.excel.model.modelRunData
import com.pharbers.aqll.calc.excel.model.integratedData

object SplitResultFunc {
	def apply(mrd : modelRunData)(avg : List[(String, Double, Double)]) = {
		avg.find (p => p._1 == mrd.segment).map { x => 
			if (mrd.ifPanelAll.equals("1")) {
            	mrd.finalResultsValue = mrd.sumValue
                mrd.finalResultsUnit = mrd.volumeUnit
            }else{
                mrd.finalResultsValue = x._2 * mrd.westMedicineIncome * mrd.factor.toDouble
                mrd.finalResultsUnit = x._3 * mrd.westMedicineIncome * mrd.factor.toDouble
            }
			
			Some(mrd.uploadYear, mrd.uploadMonth, 
			        (mrd.finalResultsValue, mrd.finalResultsUnit), 
			        (mrd.phaid), (mrd.minimumUnitCh, mrd.finalResultsValue, mrd.finalResultsUnit), (mrd.market1Ch), mrd.selectvariablecalculation().get._1)
		}.getOrElse (None)
	}
}