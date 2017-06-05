package com.pharbers.aqll.alCalc.almain

import com.pharbers.aqll.alCalaHelp.DefaultData
import com.pharbers.aqll.alCalc.almodel.java.IntegratedData
import com.pharbers.aqll.alCalc.almodel.scala.westMedicineIncome
import com.pharbers.aqll.common.alString.alStringOpt._

/**
  * Created by Alfred on 13/03/2017.
  */
object alShareData {
    def hospdata(name: String, company: String) = DefaultData.hospdatabase(name, company)

    val txt2IntegratedData : Any => IntegratedData = { txt =>
        val t = new IntegratedData()
        val x = txt.asInstanceOf[String].split(31.toChar)
        t.setHospNum(removeSpace(x(0)).toInt)
        t.setHospName(removeSpace(x(1)))
        t.setYearAndmonth(removeSpace(x(2)).toInt)
        t.setMinimumUnit(removeSpace(x(3)))
        t.setMinimumUnitCh(removeSpace(x(4)))
        t.setMinimumUnitEn(removeSpace(x(5)))
        t.setPhaid(removeSpace(x(6)))
        t.setStrength(removeSpace(x(7)))
        t.setMarket1Ch(removeSpace(x(8)))
        t.setMarket1En(removeSpace(x(9)))
        t.setSumValue(removeSpace(x(10)).toDouble)
        t.setVolumeUnit(removeSpace(x(11)).toDouble)
        t
    }

    val txt2WestMedicineIncome2 : Any => westMedicineIncome = { txt =>
        westMedicineIncome.fromString(txt.asInstanceOf[String])
    }
}
