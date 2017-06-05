package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.alexception.alException
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.alSplitStrategy
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}

/**
  * Created by Alfred on 10/03/2017.
  * 　Modify by clock on 05/06/2017.
  */
class alSplitPrecess(val strategy : alSplitStrategy) extends alPrecess {
    def precess(j : alStage) : List[alStage] = {
        try {
            j match {
                case _ : alInitStage => alException(errorToJson("not memory stage cannot precess"));Nil
                case _ : alPresisStage => alException(errorToJson("not memory stage cannot precess"));Nil
                case _ : alMemoryStage => {
                    j.storages.map ({
                        x => alStage(x.asInstanceOf[alStorage].portion(strategy.strategy).upgrade)
                    })
                }
            }

        } catch {
            case ex : OutOfMemoryError => alException(errorToJson("not enough memory")); throw ex
            case ex : Exception => alException(errorToJson("unknow error")); throw ex
        }

    }
}