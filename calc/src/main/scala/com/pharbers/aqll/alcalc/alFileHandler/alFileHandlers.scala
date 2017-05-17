package com.pharbers.aqll.alcalc.alFileHandler

import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.common.alFileHandler.alFileHandler

import scala.collection.mutable.ListBuffer

/**
  * Created by Alfred on 09/03/2017.
  */
trait alFileHandlers extends alFileHandler{
//    def prase(path : String)(x : Any) : Any = null
    def sync(path : String, s : alStorage, f : Option[String]) = Unit

//    var data : ListBuffer[Any] = ListBuffer()
}