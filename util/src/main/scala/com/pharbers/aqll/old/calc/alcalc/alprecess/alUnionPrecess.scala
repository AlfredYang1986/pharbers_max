package com.pharbers.aqll.old.calc.alcalc.alprecess

import com.pharbers.aqll.old.calc.alcalc.aldata.alStorage
import com.pharbers.aqll.old.calc.alcalc.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}


class alUnionPrecess extends alPrecess {
    def precess(j : alStage) : List[alStage] = {

        try {
            j match {
                case _ : alInitStage => ???
                case _ : alPresisStage => ???
                case _ : alMemoryStage => 
                    alStage(alStorage.combine((j.storages.asInstanceOf[List[alStorage]])) :: Nil) :: Nil
            }

        } catch {
            case ex : OutOfMemoryError => println("not enough memory"); throw ex
//            case ex : Exception => println("unknow error"); throw ex
            case ex : Exception => ex.printStackTrace; throw ex
        }
    }

    def action(j : alStage) = {
        println("presist stage is map precess")
        throw new Exception("read excel is map precess")
    }
}