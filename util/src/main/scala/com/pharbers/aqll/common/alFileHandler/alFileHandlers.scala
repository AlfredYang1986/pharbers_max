package com.pharbers.aqll.common.alFileHandler

import scala.collection.mutable.ListBuffer

trait alFileHandler {
    def prase(path : String)(x : Any) : Any = null

    def write: Unit = null

    var data : ListBuffer[Any] = ListBuffer()
}