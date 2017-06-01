package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}
import com.pharbers.aqll.alCalcOther.alLog.alLoggerMsgTrait
import com.pharbers.aqll.common.alErrorCode.alErrorCode.errorToJson


class alUnionPrecess extends alPrecess with alLoggerMsgTrait{
    def precess(j : alStage) : List[alStage] = {

        try {
            j match {
                case _ : alInitStage => logger.error(errorToJson("not memory stage cannot precess").toString);null
                case _ : alPresisStage => logger.error(errorToJson("not memory stage cannot precess").toString);null
                case _ : alMemoryStage => 
                    alStage(alStorage.combine((j.storages.asInstanceOf[List[alStorage]])) :: Nil) :: Nil
            }

        } catch {
            case ex : OutOfMemoryError => logger.info("not enough memory"); throw ex
//            case ex : Exception => println("unknow error"); throw ex
            case ex : Exception => ex.printStackTrace; throw ex
        }
    }
}