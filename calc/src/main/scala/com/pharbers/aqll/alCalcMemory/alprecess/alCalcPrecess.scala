package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.aldata.{alInitStorage, alStorage}
import com.pharbers.aqll.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}


/**
  * Created by Alfred on 10/03/2017.
  */
class alCalcPrecess extends alPrecess {
    def precess(j : alStage) : List[alStage] = {

        try {
            j match {
                case _ : alInitStage => ???
                case _ : alPresisStage => ???
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
            case ex : OutOfMemoryError => logger.info("not enough memory"); throw ex
            case ex : Exception => logger.info("unknow error"); throw ex
        }
    }

    def action(j : alStage) = {
        logger.info("presist stage is map precess")
        throw new Exception("read excel is map precess")
    }
}