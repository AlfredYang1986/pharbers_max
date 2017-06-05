package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.alstages._
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.alexception.alException
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

/*
 * 　Modify by clock on 05/06/2017.
 */
class alDistinctPrecess extends alPrecess {
    def precess(j: alStage): List[alStage] = {
        try {
            j match {
                case _: alInitStage => {
                    alException(errorToJson("not memory stage cannot precess"))
                    Nil
                }
                case _: alPresisStage => {
                    alException(errorToJson("not memory stage cannot precess"))
                    Nil
                }
                case _: alMemoryStage => {
                    val ns = j.storages.map { x =>
                        x.asInstanceOf[alStorage].distinct
                    }
                    alStage(ns) :: Nil
                }
            }
        } catch {
            case ex: OutOfMemoryError => alException(errorToJson("not enough memory")); throw ex
            case ex: Exception => alException(errorToJson("unknow error")); throw ex
        }
    }
}