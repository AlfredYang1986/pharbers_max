package com.pharbers.aqll.alCalcMemory.alprecess

import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.alCalcMemory.alprecess.alsplitstrategy.alSplitStrategy
import com.pharbers.aqll.alCalcMemory.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}

import scala.collection.JavaConverters._


/**
  * Created by Alfred on 10/03/2017.
  */
class alSplitPrecess(val strategy : alSplitStrategy) extends alPrecess {
    def precess(j : alStage) : List[alStage] = {
        try {
            j match {
                case _: alInitStage => ???
                case _: alPresisStage => ???
                case _: alMemoryStage => {
                    j.storages.map({
                        x => alStage(x.asInstanceOf[alStorage].portion(strategy.strategy).upgrade)
                    })
                }
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