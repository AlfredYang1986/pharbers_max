package com.pharbers.aqll.calc.split

import com.pharbers.aqll.calc.excel.model.modelRunData
import com.pharbers.aqll.calc.excel.model.integratedData

object SplitResultFunc {
	def apply(mrd : modelRunData)(avg : List[(String, Double, Double)]) = {
//    var i = 0
		avg.find (p => p._1 == mrd.segment).map { x =>
			if (mrd.ifPanelAll.equals("1")) {
            	  mrd.finalResultsValue = mrd.sumValue
                mrd.finalResultsUnit = mrd.volumeUnit
            }else{

                mrd.finalResultsValue = x._2 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble
                mrd.finalResultsUnit = x._3 * mrd.selectvariablecalculation.get._2 * mrd.factor.toDouble
            }
			Some(mrd.yearAndmonth.asInstanceOf[Integer],
			        (mrd.finalResultsValue, mrd.finalResultsUnit), 
			        (mrd.phaid), (mrd.minimumUnitCh), (mrd.market1Ch), mrd.selectvariablecalculation().get._1, mrd.prefecture, mrd.ifPanelAll, mrd.ifPanelTouse, mrd.segment)
		}.getOrElse (None)
	}
}