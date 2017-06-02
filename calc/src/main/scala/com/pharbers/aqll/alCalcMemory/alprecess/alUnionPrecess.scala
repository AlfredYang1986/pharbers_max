package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}


class alUnionPrecess extends alPrecess {
    def precess(j : alStage) : List[alStage] = {

        try {
            j match {
                case _ : alInitStage => logger.error(errorToJson("not memory stage cannot precess").toString);Nil
                case _ : alPresisStage => logger.error(errorToJson("not memory stage cannot precess").toString);Nil
                case _ : alMemoryStage => 
                    alStage(alStorage.combine((j.storages.asInstanceOf[List[alStorage]])) :: Nil) :: Nil
            }

        } catch {
            case ex : OutOfMemoryError => logger.info(errorToJson("not enough memory").toString()); throw ex
            case ex : Exception => logger.info(errorToJson("unknow error").toString()); throw ex
        }
    }
}