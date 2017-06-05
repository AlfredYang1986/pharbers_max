package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.alexception.alException
import com.pharbers.aqll.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

/**
  * Created by Alfred on 13/03/2017.
  * 　Modify by clock on 05/06/2017.
  */
class alFilterPrecess(f : Any => Boolean) extends alPrecess {
    def precess(j : alStage) : List[alStage] = {

        try {
            j match {
                case _ : alInitStage => {
                    alException(errorToJson("not memory stage cannot precess"))
                    Nil
                }
                case _ : alPresisStage => {
                    alException(errorToJson("not memory stage cannot precess"))
                    Nil
                }
                case _ : alMemoryStage => {
                    val ns = j.storages.map { x =>
                        x.asInstanceOf[alStorage].filter(f)
                    }
                    alStage(ns) :: Nil
                }
            }

        } catch {
            case ex : OutOfMemoryError => alException(errorToJson("not enough memory")); throw ex
            case ex : Exception => alException(errorToJson("unknow error")); throw ex
        }
    }
}