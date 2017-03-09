package com.pharbers.aqll.alcalc.aldata

import com.pharbers.aqll.alcalc.alFileHandler.alFileHandler

/**
  * Created by Alfred on 09/03/2017.
  */
object alStorage {
//    def apply(p : List[alStorage], f : AnyRef => AnyRef) : alStorage = new alStorage(p, f)      // storage 递归
    def apply(path : String, rf : alFileHandler) : alStorage = new alFileInitStorage(rf.prase(path))
    def apply(lst : List[AnyRef]) : alStorage = new alMemoryInitStorage(lst)
}

abstract class alStorage(val parents : List[alStorage], val f : AnyRef => AnyRef) {
    var data : List[AnyRef] = Nil
    var isCalc = false
    val isPortions = false

    def portion()
    def map(f : AnyRef => AnyRef) : alStorage = null // = alStorage (this :: Nil, f)

    // 非线程安全，保证一个storage在一个线程中计算
    def doCalc(implicit rp : AnyRef => alPortion)
}

abstract class alInitStorage(fc : AnyRef => AnyRef) extends alStorage(Nil, fc) {
    override def doCalc(implicit rp : AnyRef => alPortion)
}

case class alFileInitStorage(fc : AnyRef => AnyRef) extends alInitStorage(fc) {
    override def doCalc(implicit rp : AnyRef => alPortion) = {
        data = f("0").asInstanceOf[alFileHandler].data.toList
        isCalc = true
    }
}

class alMemoryInitStorage(d : List[AnyRef]) extends alInitStorage(x => x) {
    override def doCalc(implicit rp : AnyRef => alPortion) = {
        data = d.map(f)
        isCalc = true
    }
}


class alPortionedStorage(val parents : List[alStorage], val f : AnyRef => AnyRef) {
    val isPortions = true
}
