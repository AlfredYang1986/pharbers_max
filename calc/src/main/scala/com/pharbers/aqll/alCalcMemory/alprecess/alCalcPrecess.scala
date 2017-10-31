package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.alCalcMemory.alOther.alException.alException
import com.pharbers.alCalcMemory.aldata.{alInitStorage, alStorage}
import com.pharbers.alCalcMemory.alprecess.alPrecess
import com.pharbers.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

/**
  * Created by Alfred on 10/03/2017.
  * 　Modify by clock on 05/06/2017.
  */
class alCalcPrecess extends alPrecess {
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
            case ex : OutOfMemoryError => alException(errorToJson("not enough memory")); throw ex
            case ex : Exception => alException(errorToJson("unknow error")); throw ex
        }
    }
}