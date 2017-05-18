package com.pharbers.aqll.alCalaHelp.alFileHandler.altext

import java.util.UUID

import com.pharbers.aqll.alCalaHelp.alFileHandler.alFileHandlers
import com.pharbers.aqll.alCalcMemory.aldata.alStorage
import com.pharbers.aqll.common.alFileHandler.alFilesOpt.alFileOpt


/**
  * Created by BM on 09/03/2017.
  */
object alTextSync {
    def apply(path : String, s : alStorage, f : Option[String]) = (new alTextSync).sync(path, s, f)
}

class alTextSync extends alFileHandlers with CreateInnerSync {
    val parser = CreateInnerSync

    override def sync(path : String, s : alStorage, f : Option[String]) = {
        s.doCalc
        if (s.isPortions) {
            s.portions.foreach { p =>
                val file = if (f.isEmpty) UUID.randomUUID
                           else f.get
                parser.startSync(path + "/" + file , p.data)
            }
        } else {
            val file = if (f.isEmpty) UUID.randomUUID
                       else f.get
            parser.startSync(path + "/" + file, s.data)
        }
        Unit
    }
}

case class inner_sync(h : alFileHandlers) {
    implicit val f : String => Any = x => x
    def startSync(file : String, data : List[Any]) = alFileOpt(file).pushData2File(data)
}

trait CreateInnerSync { this : alFileHandlers =>
    def CreateInnerSync : inner_sync = new inner_sync(this)
}