package com.pharbers.aqll.old.calc.alcalc.alFileHandler


import com.pharbers.aqll.old.calc.alcalc.aldata.alStorage

import scala.collection.mutable.ListBuffer

/**
  * Created by Alfred on 09/03/2017.
  */
trait alFileHandlers {
    def prase(path : String)(x : Any) : Any = null
    def sync(path : String, s : alStorage, f : Option[String]) = Unit

    var data : ListBuffer[Any] = ListBuffer()
}