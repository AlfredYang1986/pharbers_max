package com.pharbers.aqll.alCalcMemory.alstages

import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.common.alErrorCode.alErrorCode.errorToJson


/**
  * Created by Alfred on 10/03/2017.
  */

object alStage {
    def apply() : alStage = new alInitStage
    def apply(path : String) : alStage = {
        val tmp = new alPresisStage
        tmp.storages = path :: Nil
        tmp
    }
    def apply(files : List[String]) : alPresisStage = {
        val tmp = new alPresisStage
        tmp.storages = files
        tmp
    }

    def apply(data : List[alStorage]): alMemoryStage = {
        val tmp = new alMemoryStage
        tmp.storages = data
        tmp
    }
}

trait alStage extends alLoggerMsgTrait{
    var storages : List[AnyRef] = Nil
    def isCalc = false

    def canLength : Boolean = false
    def length : Int = {
        logger.error(errorToJson("only Memory can calc length").toString)
        -1
    }
}

class alInitStage extends alStage
class alMemoryStage extends alStage {
    override def canLength : Boolean = true
    override def isCalc: Boolean = storages.find (x => !x.asInstanceOf[alStorage].isCalc) == None
    override def length: Int = storages.map (x => x.asInstanceOf[alStorage].length).sum
}
class alPresisStage extends alStage
