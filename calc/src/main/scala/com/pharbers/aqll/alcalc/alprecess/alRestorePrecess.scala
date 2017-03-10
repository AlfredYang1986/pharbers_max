package com.pharbers.aqll.alcalc.alprecess

import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alfilehandler.altext.{FileOpt, alTextParser, alTextSync}
import com.pharbers.aqll.alcalc.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}

/**
  * Created by Alfred on 10/03/2017.
  */
class alRestorePrecess extends alPrecess {
    def precess(j : alStage) : List[alStage] = {
        def precessAcc(path : String) : alStorage = alStorage(path, new alTextParser)

        try {
            j match {
                case it : alInitStage => alStage(precessAcc(it.storages.head.toString) :: Nil) :: Nil
                case it : alPresisStage => alStage(it.storages.map(x => precessAcc(x.toString))) :: Nil
                case _ : alMemoryStage => ???
            }

        } catch {
            case ex : OutOfMemoryError => println("not enough memory"); throw ex
            case ex : Exception => println("unknow error"); throw ex
        }
    }

    def action(j : alStage) = {
        println("presist stage is map precess")
        throw new Exception("read excel is map precess")
    }
}