package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}


class alDistinctPrecess extends alPrecess {
    def precess(j : alStage) : List[alStage] = {
        try {
            j match {
                case _ : alInitStage => ???
                case _ : alPresisStage => ???
                case _ : alMemoryStage => {
                    val ns = j.storages.map { x =>
                        x.asInstanceOf[alStorage].distinct
                    }
                    alStage(ns) :: Nil
                }
            }

        } catch {
            case ex : OutOfMemoryError => logger.info("not enough memory"); throw ex
//            case ex : Exception => println("unknow error"); throw ex
            case ex : Exception => ex.printStackTrace; throw ex
        }
    }

    def action(j : alStage) = {
        logger.info("presist stage is map precess")
        throw new Exception("read excel is map precess")
    }
}