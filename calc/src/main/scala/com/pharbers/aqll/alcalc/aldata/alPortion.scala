package com.pharbers.aqll.alcalc.aldata

/**
  * Created by BM on 09/03/2017.
  */

object alPortion {
    def apply(data : List[Any]) : alMemoryPortion = new alMemoryPortion(data)
}

trait alPortion {
    def isPersis : Boolean

    def map(f : Any => Any) : alPortion
}

case class alMemoryPortion(val data : List[Any]) extends alPortion {
    override def isPersis = false

    def map(f : Any => Any) : alPortion = new alMemoryPortion(data.map(f))
}
case class alPersisportion(path : String) extends  alPortion {
    override def isPersis = true

    def map(f : Any => Any) : alPortion = ???
}

