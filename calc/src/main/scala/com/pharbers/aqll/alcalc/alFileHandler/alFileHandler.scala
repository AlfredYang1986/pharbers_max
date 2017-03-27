package com.pharbers.aqll.alcalc.alfilehandler

import com.pharbers.aqll.alcalc.aldata.alStorage

import scala.collection.mutable.ListBuffer

/**
  * Created by Alfred on 09/03/2017.
  */
trait alFileHandler {
    def prase(path : String)(x : Any) : Any = null
    def sync(path : String, s : alStorage, f : Option[String]) = Unit

    var data : ListBuffer[Any] = ListBuffer()
}