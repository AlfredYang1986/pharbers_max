package com.pharbers.aqll.alcalc.aldata

/**
  * Created by BM on 09/03/2017.
  */

object alPortion {
    def apply(data : List[AnyRef]) : alMemoryPortion = new alMemoryPortion(data)
}

trait alPortion {
    def isPersis : Boolean

    def map(f : AnyRef => AnyRef) : alPortion
}

case class alMemoryPortion(val data : List[AnyRef]) extends alPortion {
    override def isPersis = false

    def map(f : AnyRef => AnyRef) : alPortion = new alMemoryPortion(data.map(f))
}
case class alPersisportion(path : String) extends  alPortion {
    override def isPersis = true

    def map(f : AnyRef => AnyRef) : alPortion = ???
}

