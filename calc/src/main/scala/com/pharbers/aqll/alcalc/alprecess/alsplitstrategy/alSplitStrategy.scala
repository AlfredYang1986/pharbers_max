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

    object core_split extends strategy_defines(1, "split base on cores") {
        val core_number = "core_number"
        def apply(c : Map[String, Any]) : alCoreSplitStrategy = new alCoreSplitStrategy(c)
    }

    object hash_split extends strategy_defines(2, "hash split for cores") {
        val core_number = "core_number"
        val hash_func =
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
        // TODO: 需要一个根据内存分配的stratege去划分整体数据
        lst.grouped(lst.length).map(iter => alPortion(iter)).toList
    }
}

class alCoreSplitStrategy(val c : Map[String, Any]) extends alSplitStrategy {
    override val constraints: Map[String, Any] = c
    override val strategy : List[Any] => List[alPortion] = { lst =>
        import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.core_split
        val t = constraints.get(core_split.core_number).map (x => x.asInstanceOf[Int]).getOrElse(1)
        val sn = lst.length / t + 1
        lst.grouped(sn).map(alPortion(_)).toList
    }
}

class alHashSplitStrategy(val c : Map[String, Any]) extends  alSplitStrategy {
    override val constraints: Map[String, Any] = c
    override val strategy : List[Any] => List[alPortion] = { lst =>
        import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.core_split
        val t = constraints.get(core_split.core_number).map (x => x.asInstanceOf[Int]).getOrElse(1)
        val sn = lst.length / t + 1
        lst.grouped(sn).map(alPortion(_)).toList
    }
}
