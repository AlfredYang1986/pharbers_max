package com.pharbers.aqll.alCalcHelp

import com.pharbers.aqll.alCalcHelp.alModel.java.IntegratedData
import com.pharbers.aqll.alCalcHelp.alModel.scala.westMedicineIncome
import com.pharbers.aqll.common.alString.alStringOpt._

/**
  * Created by Alfred on 13/03/2017.
  */
object alShareData {
    def hospdata(name: String, company: String, market: String) = DefaultData.hospdatabase(name, company, market)

    val csv2IntegratedData : Any => IntegratedData = { txt =>
        val t = new IntegratedData()
        val x = txt.asInstanceOf[String].split(31.toChar)
        t.setHospNum(removeSpace(x(0)).toInt)
        t.setHospName(removeSpace(x(1)))
        t.setYearAndmonth(removeSpace(x(2)).toInt)
        t.setMinimumUnit(removeSpace(x(3)))
        t.setMinimumUnitCh(removeSpace(x(4)))
        t.setPhaid(removeSpace(x(5)))
        t.setStrength(removeSpace(x(6)))
        t.setMarket1Ch(removeSpace(x(7)))
        t.setMarket1En(removeSpace(x(8)))
        t.setVolumeUnit(removeSpace(x(9)).toDouble)
        t.setSumValue(removeSpace(x(10)).toDouble)
        t
    }

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
    
    val txtSegmentGroupData: Any => alSegmentGroup = { line =>
        alSegmentGroup.fromString(line.asInstanceOf[String])
    }

    val txt2WestMedicineIncome2 : Any => westMedicineIncome = { txt =>
        westMedicineIncome.fromString(txt.asInstanceOf[String])
    }
}

object alSegmentGroup {
    def apply(args: Any*) = {
        val tmp = new alSegmentGroup
        tmp.map = (properties zip args).toMap
        tmp
    }
    
    def fromString(args : String) = {
        val sub = args.split(31.toChar)
        val tmp = new alSegmentGroup
        sub.foreach { iter =>
            val lst = iter.split("=")
            val a = lst.head
            val b = lst.tail.head
            tmp.map += (a -> b)
        }
        tmp
    }
    
    val properties : List[String] = "segment" :: "sales" :: "unit" :: "calc" :: Nil
}

class alSegmentGroup {
    var map : Map[String, Any] = Map.empty
    
    override def toString: String = {
        val lst = alSegmentGroup.properties.map { x =>
            
            map.get(x).map(y => s"""$x=$y${31.toChar}""").getOrElse(s"""$x=${31.toChar}""")
        }
        val buf = new StringBuffer
        lst foreach ( x => buf.append(x))
        buf.toString
    }
    
    def getV(k: String): Any = map.get(k).getOrElse("无")
    
    def segement = getV("segment").toString
    
    def sales: Double = getV("sales") match {
        case d: Double => d
        case s: String => s.toString.toDouble
        case _ => println("sales fuck");???
    }
    
    def units: Double = getV("unit") match {
        case d: Double => d
        case s: String => s.toString.toDouble
        case _ => println("unit fuck");???
    }
    
    def calc: Double = getV("calc") match {
        case d: Double => d
        case s: String => s.toString.toDouble
        case _ => println("calc fuck");???
    }
}
