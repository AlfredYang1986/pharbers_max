package com.pharbers.aqll.alcalc.alprecess.alsplitstrategy

import com.pharbers.aqll.alcalc.aldata.alPortion

import scala.collection.mutable.ArrayBuffer

/**
  * Created by BM on 10/03/2017.
  */

object server_info {
    val cpu: Int = Runtime.getRuntime.availableProcessors
    val memory: Long = Runtime.getRuntime.maxMemory
}

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
        val hash_func = "hash_func"
        def apply(c : Map[String, Any]) : alHashSplitStrategy = new alHashSplitStrategy(c)
    }
}

object alServerHardware extends alHardware{
    def strategy_hardware(c: Any)(func: Any => Any) = {
        func(c)
    }
}

sealed class strategy_defines(val t : Int, val d : String)

trait alSplitStrategy {
    val strategy : List[Any] => List[alPortion]
    val constraints : Map[String, Any]
}

trait alHardware {
    val server_memory = "server_memory"
    val core_number = "core_number"
    val strategy_memeory: Any => Long = { c =>
        c.asInstanceOf[Long]
    }

    val strategy_core: Any => Int = { c =>
        c.asInstanceOf[Int]
    }
}

class alReadExcelSplitStrategy(val c : Map[String, Any]) extends alSplitStrategy {
    override val constraints: Map[String, Any] = c
    override val strategy : List[Any] => List[alPortion] = { lst =>
        import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.read_excel_split
        import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.hash_split
        // TODO: 需要一个根据内存分配的stratege去划分整体数据
        val memory = constraints.get(alServerHardware.server_memory)
        memory match {
            case None => {
                val section_number = constraints.get(read_excel_split.section_number).map(_.asInstanceOf[Int]).getOrElse(1)
                val hash_func = constraints.get(hash_split.hash_func).map (x => x.asInstanceOf[Any => Int]).getOrElse(throw new Exception("need have func"))
                val re = (1 to section_number).map(_ => ArrayBuffer[Any]())
                //val size = (lst.size / section_number) + 1
                lst foreach { iter =>
                    val i = hash_func(iter) % section_number
                    re(i).append(iter)
                }
                re.map (x => alPortion(x.toList)).toList
                //lst.grouped(size).map(iter => alPortion(iter)).toList
            }
            case Some(x) => {
                val size = 2 * 80 * 53 * 300
                val number = (x.asInstanceOf[Long] / size).toInt
                lst.grouped(number).map(iter => alPortion(iter)).toList
            }
        }
//        val section_number = constraints.get(read_excel_split.section_number).get.asInstanceOf[Int]
//        println(s"memory = $memory")
//        // val size = 2 * 80 * 53 * 100
//        val size = 2 * 80 * 53 * 300
//        println(s"size = $size")
//        val number = (memory / size).toInt
//        println(s"number = $number")
//        lst.grouped(number).map(iter => alPortion(iter)).toList
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
        import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy.hash_split
        val t = constraints.get(hash_split.core_number).map (x => x.asInstanceOf[Int]).getOrElse(1)
        val hash_func = constraints.get(hash_split.hash_func).map (x => x.asInstanceOf[Any => Int]).getOrElse(throw new Exception("should have func"))
        println(s"fucking 啊哈 = $c")
        println(s"fucking 啊哈 = $hash_func")
        println(s"fucking 啊哈 = $t")
        val re = (1 to t).map(_ => ArrayBuffer[Any]())
        lst foreach { iter =>
            val i = hash_func(iter) % t
            re(i).append(iter)
        }
        println(s"fucking 啊哈 = ${re.size}")
        re.map (x => alPortion(x.toList)).toList
    }
}
