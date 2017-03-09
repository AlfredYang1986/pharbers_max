package com.pharbers.aqll.alcalc.alFileHandler.altext

import com.pharbers.aqll.alcalc.alFileHandler.alFileHandler
import com.pharbers.aqll.alcalc.aldata.{alMemoryPortion, alStorage, alPersisportion}

/**
  * Created by BM on 09/03/2017.
  */
object alTextSync {
    def apply(path : String, s : alStorage) = (new alTextSync).sync(path, s)
}

class alTextSync extends alFileHandler with CreateInnerSync {
    val parser = CreateInnerSync

    override def sync(path : String, s : alStorage) = {
        s.doCalc
        if (s.isPortions) {
            s.portions.foreach { p => p match {
                case x : alMemoryPortion => parser.startSync(path + "/po", x.data)
                case x : alPersisportion => ???
            }}
        } else {
            parser.startSync(path + "/po", s.data)
        }
        Unit
    }
}

case class inner_sync(h : alFileHandler) {
    implicit val f : String => Any = x => x
    def startSync(file : String, data : List[Any]) = FileOpt(file).pushData2File(data)
}

trait CreateInnerSync { this : alFileHandler =>
    def CreateInnerSync : inner_sync = new inner_sync(this)
}