package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.aldata.{alInitStorage, alStorage}
import com.pharbers.aqll.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.common.alErrorCode.alErrorCode.errorToJson


/**
  * Created by Alfred on 10/03/2017.
  */
class alCalcPrecess extends alPrecess with alLoggerMsgTrait{
    def precess(j : alStage) : List[alStage] = {

        try {
            j match {
                case _ : alInitStage => logger.error(errorToJson("not memory stage cannot precess").toString);null
                case _ : alPresisStage => logger.error(errorToJson("not memory stage cannot precess").toString);null
                case _ : alMemoryStage => {
                    val ns = j.storages.map { x =>
                        val tmp = x.asInstanceOf[alStorage]
                        tmp.doCalc

                        if (tmp.isInstanceOf[alInitStorage]) {
                            val t = alStorage(tmp.data)
                            t.doCalc
                            t
                        }
                        else tmp
                    }
                    alStage(ns) :: Nil
                }
            }

        } catch {
            case ex : OutOfMemoryError => logger.error(errorToJson("not enough memory").toString); throw ex
            case ex : Exception => logger.error(errorToJson("unknow error").toString + ex.getMessage); throw ex
        }
    }
}