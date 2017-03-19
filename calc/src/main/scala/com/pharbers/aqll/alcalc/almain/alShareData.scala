package com.pharbers.aqll.alcalc.almain

import com.pharbers.aqll.calc.common.DefaultData
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.excel.model.westMedicineIncome
import com.pharbers.aqll.calc.util.StringOption

/**
  * Created by Alfred on 13/03/2017.
  */
object alShareData {
    lazy val hospdata = DefaultData.hospdatabase("universe_ot_SPE_ljx.xlsx")

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

    val txt2WestMedicineIncome : Any => westMedicineIncome = { txt =>
        val x = txt.asInstanceOf[String].split(31.toChar)
//        println(txt)
//        println(x.length)
//        println("1 : " + StringOption.takeStringSpace(x(0)))
//        println("2 : " + StringOption.takeStringSpace(x(1)))
//        println("3 : " + StringOption.takeStringSpace(x(2)))
//        println("4 : " + StringOption.takeStringSpace(x(3)))
//        println("5 : " + StringOption.takeStringSpace(x(4)))
//        println("6 : " + StringOption.takeStringSpace(x(5)))
//        println("7 : " + StringOption.takeStringSpace(x(6)))
//        println("8 : " + StringOption.takeStringSpace(x(7)))
//        println("9 : " + StringOption.takeStringSpace(x(8)))
//        println("10 : " + StringOption.takeStringSpace(x(9)))
//        println("11 : " + StringOption.takeStringSpace(x(10)))
//        println("12 : " + StringOption.takeStringSpace(x(11)))
//        println("13 : " + StringOption.takeStringSpace(x(12)))
//        println("14 : " + StringOption.takeStringSpace(x(13)))
//        println("15 : " + StringOption.takeStringSpace(x(14)))
//        println("16 : " + StringOption.takeStringSpace(x(15)))
//        println("17 : " + StringOption.takeStringSpace(x(16)))
//        println("18 : " + StringOption.takeStringSpace(x(17)))
//        println("19 : " + StringOption.takeStringSpace(x(18)))
//        println("20 : " + StringOption.takeStringSpace(x(19)))
//        println("21 : " + StringOption.takeStringSpace(x(20)))
//        println("22 : " + StringOption.takeStringSpace(x(21)))
//        println("23 : " + StringOption.takeStringSpace(x(22)))
//        println("24 : " + StringOption.takeStringSpace(x(23)))
//        println("25 : " + StringOption.takeStringSpace(x(24)))
//        println("26 : " + StringOption.takeStringSpace(x(25)))
//        println("27 : " + StringOption.takeStringSpace(x(26)))
//        println("28 : " + StringOption.takeStringSpace(x(27)))
//        println("29 : " + StringOption.takeStringSpace(x(28)))
//        println("30 : " + StringOption.takeStringSpace(x(29)))
//        println("31 : " + StringOption.takeStringSpace(x(30)))
//        println("32 : " + StringOption.takeStringSpace(x(31)))
//        println("33 : " + StringOption.takeStringSpace(x(32)))
//        println("34 : " + StringOption.takeStringSpace(x(33)))
//        println("35 : " + StringOption.takeStringSpace(x(34)))
//        println("36 : " + StringOption.takeStringSpace(x(35)))
//        println("37 : " + StringOption.takeStringSpace(x(36)))
//        println("38 : " + StringOption.takeStringSpace(x(37)))
//        println("39 : " + StringOption.takeStringSpace(x(38)))
//        println("40 : " + StringOption.takeStringSpace(x(39)))
//        println("41 : " + StringOption.takeStringSpace(x(40)))
//        println("42 : " + StringOption.takeStringSpace(x(41)))
//        println("43 : " + StringOption.takeStringSpace(x(42)))
//        println("44 : " + StringOption.takeStringSpace(x(43)))
//        println("45 : " + StringOption.takeStringSpace(x(44)))
//        println("46 : " + StringOption.takeStringSpace(x(45)))
//        println("47 : " + StringOption.takeStringSpace(x(46)))
//        println("48 : " + StringOption.takeStringSpace(x(47)))
//        println("49 : " + StringOption.takeStringSpace(x(48)))
//        println("50 : " + StringOption.takeStringSpace(x(49)))
//        println("51 : " + StringOption.takeStringSpace(x(50)))
//        println("52 : " + StringOption.takeStringSpace(x(51)))
//        println("53 : " + StringOption.takeStringSpace(x(52)))

        new westMedicineIncome(
            StringOption.takeStringSpace(x(0).split("=")(1)),
            StringOption.takeStringSpace(x(1).split("=")(1)).toInt,
            StringOption.takeStringSpace(x(2).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(3).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(4).split("=")(1)),
            StringOption.takeStringSpace(x(5).split("=")(1)),
            StringOption.takeStringSpace(x(6).split("=")(1)),
            StringOption.takeStringSpace(x(7).split("=")(1)),
            StringOption.takeStringSpace(x(8).split("=")(1)),
            StringOption.takeStringSpace(x(9).split("=")(1)),
            StringOption.takeStringSpace(x(10).split("=")(1)),
            StringOption.takeStringSpace(x(11).split("=")(1)),
            StringOption.takeStringSpace(x(12).split("=")(1)),
            StringOption.takeStringSpace(x(13).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(14).split("=")(1)),
            StringOption.takeStringSpace(x(15).split("=")(1)),
            StringOption.takeStringSpace(x(16).split("=")(1)),
            StringOption.takeStringSpace(x(17).split("=")(1)),
            StringOption.takeStringSpace(x(18).split("=")(1)),
            StringOption.takeStringSpace(x(19).split("=")(1)),
            StringOption.takeStringSpace(x(20).split("=")(1)),
            StringOption.takeStringSpace(x(21).split("=")(1)),
            StringOption.takeStringSpace(x(22).split("=")(1)),
            StringOption.takeStringSpace(x(23).split("=")(1)),
            StringOption.takeStringSpace(x(24).split("=")(1)),
            StringOption.takeStringSpace(x(25).split("=")(1)),
            StringOption.takeStringSpace(x(26).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(27).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(28).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(29).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(30).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(31).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(32).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(33).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(34).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(35).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(36).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(37).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(38).split("=")(1)).toLong,
            StringOption.takeStringSpace(x(39).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(40).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(41).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(42).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(43).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(44).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(45).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(46).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(47).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(48).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(49).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(50).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(51).split("=")(1)).toDouble,
            StringOption.takeStringSpace(x(52).split("=")(1)).toDouble
        )
    }
}
