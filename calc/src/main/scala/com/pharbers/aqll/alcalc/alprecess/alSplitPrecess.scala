package com.pharbers.aqll.alcalc.alprecess

import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alFilehandler.altext.alTextParser
import com.pharbers.aqll.alcalc.alprecess.alsplitstrategy.alSplitStrategy
import com.pharbers.aqll.alcalc.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}

/**
  * Created by Alfred on 10/03/2017.
  */
class alSplitPrecess(val strategy : alSplitStrategy) extends alPrecess {
    def precess(j : alStage) : List[alStage] = {
        try {
            j match {
                case _ : alInitStage => ???
                case _ : alPresisStage => ???
                case _ : alMemoryStage => {
                    j.storages.map ({
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