package com.pharbers.aqll.alcalc.alprecess

import java.util.UUID

import com.pharbers.aqll.alcalc.aldata.{alPortion, alStorage}
import com.pharbers.aqll.alcalc.alfilehandler.alexcel.alIntegrateddataparser
import com.pharbers.aqll.alcalc.alfilehandler.altext.{FileOpt, alTextSync}
import com.pharbers.aqll.alcalc.alstages.{alInitStage, alMemoryStage, alPresisStage, alStage}

/**
  * Created by Alfred on 10/03/2017.
  */
class alPresistStagePrecess extends alPrecess {
    def precess(j : alStage) : List[alStage] = {

        val dir = UUID.randomUUID

        val f = FileOpt(s"""config/sync/$dir""")
        f.createDir
        j.storages map { x =>
            alTextSync(s"""config/sync/$dir""", x.asInstanceOf[alStorage])
        }
        //val ss = f.lstFiles.map(alPortion(_))
        //alStage(alStorage(ss) :: Nil) :: Nil

        alStage(f.lstFiles) :: Nil
    }

    def action(j : alStage) = {
        println("presist stage is map precess")
        throw new Exception("read excel is map precess")
    }
}