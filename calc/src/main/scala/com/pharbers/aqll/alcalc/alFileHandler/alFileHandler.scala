package com.pharbers.aqll.alcalc.alFileHandler

import akka.actor.Actor
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData

import scala.collection.mutable.ListBuffer

/**
  * Created by Alfred on 09/03/2017.
  */
trait alFileHandler {
    def prase(path : String)(x : AnyRef) : alFileHandler

    var data : ListBuffer[AnyRef] = ListBuffer()
}