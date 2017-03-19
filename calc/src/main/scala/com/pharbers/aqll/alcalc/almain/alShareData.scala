package com.pharbers.aqll.alcalc.almain

import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.excel.model.westMedicineIncome2
import com.pharbers.aqll.calc.util.StringOption

/**
  * Created by Alfred on 13/03/2017.
  */
object alShareData {
    lazy val hospdata = DefaultData.hospdatabase("20000123456.xlsx")

    val txt2IntegratedData : Any => IntegratedData = { txt =>
        val t = new IntegratedData()
        val x = txt.asInstanceOf[String].split(",")
        t.setHospNum(StringOption.takeStringSpace(x(0)).toInt)
        t.setHospName(StringOption.takeStringSpace(x(1)))
        t.setYearAndmonth(StringOption.takeStringSpace(x(2)).toInt)
        t.setMinimumUnit(StringOption.takeStringSpace(x(3)))
        t.setMinimumUnitCh(StringOption.takeStringSpace(x(4)))
        t.setMinimumUnitEn(StringOption.takeStringSpace(x(5)))
        t.setPhaid(StringOption.takeStringSpace(x(6)))
        t.setStrength(StringOption.takeStringSpace(x(7)))
        t.setMarket1Ch(StringOption.takeStringSpace(x(8)))
        t.setMarket1En(StringOption.takeStringSpace(x(9)))
        t.setSumValue(StringOption.takeStringSpace(x(10)).toDouble)
        t.setVolumeUnit(StringOption.takeStringSpace(x(11)).toDouble)
        t
    }

    val txt2WestMedicineIncome2 : Any => westMedicineIncome2 = { txt =>
        westMedicineIncome2.fromString(txt.asInstanceOf[String])
    }
}
