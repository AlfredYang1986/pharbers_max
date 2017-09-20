package com.pharbers.aqll.alCalcMemory.alprecess

import java.util.UUID

import com.pharbers.aqll.alCalaHelp.alFileHandler.altext.alTextSync
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.alCalcMemory.alstages.alStage
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt

/**
  * Created by Alfred on 10/03/2017.
  */
class alPresistStagePrecess(val dirOpt : Option[String], val prefix : Option[String], val nameOpt : Option[String]) extends alPrecess {
    var reVal : Option[(String, List[String])] = None  // (dir, files)

    def precess(j : alStage) : List[alStage] = {

        val dir = dirOpt.map (x => x).getOrElse(UUID.randomUUID.toString)
        val prefixdir = prefix.map (x => x).getOrElse(sync)
	    val path = s"${memorySplitFile}$prefixdir/$dir"
        val f = alFileOpt(path)
        f.createDir
        j.storages map { x =>
            alTextSync(path, x.asInstanceOf[alStorage], nameOpt)
        }
        //val ss = f.lstFiles.map(alPortion(_))
        //alStage(alStorage(ss) :: Nil) :: Nil

        val files = f.listAllFiles.map (x => x.drop(x.lastIndexOf("/") + 1))
        reVal = Some((dir, files))
        alStage(files) :: Nil
    }

    override def result: Option[Any] = reVal
}