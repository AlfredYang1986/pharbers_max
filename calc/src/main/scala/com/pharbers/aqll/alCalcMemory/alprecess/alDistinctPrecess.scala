package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.alstages._
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

class alDistinctPrecess extends alPrecess {
    def precess(j : alStage) : List[alStage] = {
        try {
            j match {
                case _ : alInitStage => {
                    logger.error(errorToJson("not memory stage cannot precess").toString)
                    Nil
                }
                case _ : alPresisStage => {
                    logger.error(errorToJson("not memory stage cannot precess").toString)
                    Nil
                }
                case _ : alMemoryStage => {
                    val ns = j.storages.map { x =>
                        x.asInstanceOf[alStorage].distinct
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