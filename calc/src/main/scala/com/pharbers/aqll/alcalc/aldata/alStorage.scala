package com.pharbers.aqll.alcalc.aldata

import com.pharbers.aqll.alcalc.alFileHandler.alFileHandler

/**
  * Created by Alfred on 09/03/2017.
  */
object alStorage {
    def apply(path : String, rf : alFileHandler) : alStorage = new alFileInitStorage(rf.prase(path))
    def apply(lst : List[Any]) : alStorage = new alMemoryInitStorage(lst)
}

abstract class alStorage(val parents : List[alStorage], val f : Any => Any) {
    var data : List[Any] = Nil
    var portions : List[alPortion] = Nil
    var isCalc = false
    val isPortions = false

    def portion(ps : List[Any] => List[alPortion]) : alPortionedStorage = {
        if (!isCalc) {
            doCalc
            portion(ps)
        } else {
            val tmp = new alPortionedStorage(this :: Nil, f)
            tmp.portions = ps(data)
            tmp.isCalc = true
            tmp
        }
    }

    // Map 计算
    def map(f : Any => Any) : alStorage = {
        if (isPortions) new alPortionedStorage(this :: Nil, f)
        else new alNormalStorage(this :: Nil, f)
    }

    // 计算
    def doCalc
}

abstract class alInitStorage(fc : Any => Any) extends alStorage(Nil, fc) {
    override def doCalc
}

case class alFileInitStorage(fc : Any => Any) extends alInitStorage(fc) {
    override def doCalc {
        if (!isCalc) {
            data = f("0").asInstanceOf[alFileHandler].data.toList
            isCalc = true
        }
    }
}

class alMemoryInitStorage(d : List[Any]) extends alInitStorage(null) {
    override def doCalc = {
        if (!isCalc) {
            data = d
            isCalc = true
        }
    }
}

class alPortionedStorage(p : List[alStorage], fc : Any => Any) extends alStorage(p, fc) {
    override val isPortions = true

    override def doCalc = {
        if (!isCalc) {
            p.foreach(_.doCalc)

            parents match {
                case Nil => portions = portions.map (iter => iter.map(f))
                case pt :: Nil => portions = pt.portions.map (iter => iter.map(f))
                case _ => println("not implement"); ???
            }
            isCalc = true
        }
    }
}

class alNormalStorage(p : List[alStorage], fc : Any => Any) extends alStorage(p, fc) {
    override def doCalc {
        if (!isCalc) {
            p.foreach(_.doCalc)

            parents match {
                case Nil => data = data.map(f)
                case pt :: Nil => {
                    data = pt.data.map(f)
                }
                case _ => println("not implement"); ???
            }
            isCalc = true
        }
    }
}
