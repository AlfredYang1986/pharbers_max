package com.pharbers.aqll.alcalc.alprecess.alsplitstrategy

import com.pharbers.aqll.alcalc.aldata.alPortion

/**
  * Created by BM on 10/03/2017.
  */


object alSplitStrategy {
    object read_excel_split extends strategy_defines(0, "read excel split strategy") {
        val section_number = "section_number"
        def apply(c : Map[String, Any]) : alReadExcelSplitStrategy = new alReadExcelSplitStrategy(c)
    }
}

sealed class strategy_defines(val t : Int, val d : String)

trait alSplitStrategy {
    val strategy : List[Any] => List[alPortion]
    val constraints : Map[String, Any]
}

class alReadExcelSplitStrategy(val c : Map[String, Any]) extends alSplitStrategy {
    override val constraints: Map[String, Any] = c
    override val strategy : List[Any] => List[alPortion] = { lst =>
        import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.read_excel_split
        val t = constraints.get(read_excel_split.section_number).map (x => x.asInstanceOf[Int]).getOrElse(1)
        val sn = lst.length / t + 1
        lst.grouped(sn).map(alPortion(_)).toList
    }
}
