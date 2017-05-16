package com.pharbers.aqll.alcalc.alprecess

import java.util.UUID

import com.pharbers.aqll.alcalc.aldata.alStorage
import com.pharbers.aqll.alcalc.alFileHandler.altext.{FileOpt, alTextSync}
import com.pharbers.aqll.alcalc.alstages.alStage
import com.pharbers.aqll.util.fileConfig._

/**
  * Created by Alfred on 10/03/2017.
  */
class alPresistStagePrecess(val dirOpt : Option[String], val prefix : Option[String], val nameOpt : Option[String]) extends alPrecess {
    var reVal : Option[(String, List[String])] = None  // (dir, files)

    def precess(j : alStage) : List[alStage] = {

        val dir = dirOpt.map (x => x).getOrElse(UUID.randomUUID.toString)
        val syncdir = prefix.map (x => x).getOrElse(sync)
//        val path = s"config/$sync/$dir"
	    val path = s"${memorySplitFile}$syncdir/$dir"
        val f = FileOpt(path)
        f.createDir
        j.storages map { x =>
            alTextSync(path, x.asInstanceOf[alStorage], nameOpt)
        }
        //val ss = f.lstFiles.map(alPortion(_))
        //alStage(alStorage(ss) :: Nil) :: Nil

        val files = f.lstFiles.map (x => x.drop(x.lastIndexOf("/") + 1))
        reVal = Some((dir, files))
        alStage(files) :: Nil
    }

    def action(j : alStage) = {
        println("presist stage is map precess")
        throw new Exception("read excel is map precess")
    }

    override def result: Option[Any] = reVal
}