package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalaHelp.alFileHandler.altext.alTextParser
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
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
                case _ : alMemoryStage => ???
            }

        } catch {
            case ex : OutOfMemoryError => logger.info("not enough memory"); throw ex
            case ex : Exception => logger.info("unknow error"); throw ex
        }
    }

    def action(j : alStage) = {
        logger.info("presist stage is map precess")
        throw new Exception("read excel is map precess")
    }
}