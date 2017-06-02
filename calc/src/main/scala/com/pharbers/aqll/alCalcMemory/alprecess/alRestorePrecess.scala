package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalaHelp.alFileHandler.altext.alTextParser
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.common.alErrorCode.alErrorCode._
import com.pharbers.aqll.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}

/**
  * Created by Alfred on 10/03/2017.
  */
class alRestorePrecess extends alPrecess {
    def pathProxy(path : String) : String = path
    
    def precess(j : alStage) : List[alStage] = {
        def precessAcc(path : String) : alStorage = alStorage(pathProxy(path), new alTextParser)

        try {
            j match {
                case it : alInitStage => alStage(precessAcc(it.storages.head.toString) :: Nil) :: Nil
                case it : alPresisStage => alStage(it.storages.map(x => precessAcc(x.toString))) :: Nil
                case _ : alMemoryStage => logger.error(errorToJson("memory stage cannot precess").toString); Nil
            }

        } catch {
            case ex : OutOfMemoryError => logger.error(errorToJson("not enough memory").toString); throw ex
            case ex : Exception => logger.error(errorToJson("unknow error").toString + ex.getMessage); throw ex
        }
    }
}