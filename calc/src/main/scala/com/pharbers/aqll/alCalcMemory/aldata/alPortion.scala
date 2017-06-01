package com.pharbers.aqll.alCalcMemory.aldata

import com.pharbers.aqll.alCalaHelp.alFileHandler.altext.alTextParser
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.common.alErrorCode.alErrorCode.errorToJson

/**
  * Created by BM on 09/03/2017.
  * Modify by clock on 24/05/2017.
  */

object alPortion {
    def apply(data : List[Any]) : alMemoryPortion = new alMemoryPortion(data)
    def apply(path : String) : alPortion = new alPersisportion(path)
    
    def union(lst : List[alPortion]) : alPortion = {
        def unionAcc(cur : List[alPortion]) : List[Any] = {
            cur match {
                case Nil => Nil
                case head :: tail => head.data ::: unionAcc(tail)
            }
        }
        
        alPortion(unionAcc(lst))
    }
}

trait alPortion extends alLoggerMsgTrait{
    def isPersis : Boolean

    def map(f : Any => Any) : alPortion
    def data : List[Any]
    def length : Int
}

case class alMemoryPortion(val d : List[Any]) extends alPortion {
    override def isPersis = false

    def map(f : Any => Any) : alPortion = new alMemoryPortion(d.map(f))
    def data : List[Any] = d
    def length : Int = d.length
}
case class alPersisportion(path : String) extends  alPortion with alLoggerMsgTrait{
    override def isPersis = true

    def map(f : Any => Any) : alPortion = {
        logger.error(errorToJson("persist portion cannot map").toString)
        null
    }
    def data : List[Any] = alPortion(alTextParser(path)).data
    def length : Int = {
        logger.error(errorToJson("persist portion cannot calc length").toString)
        -1
    }
}

