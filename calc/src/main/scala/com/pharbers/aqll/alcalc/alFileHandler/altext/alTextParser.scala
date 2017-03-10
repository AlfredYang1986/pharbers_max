package com.pharbers.aqll.alcalc.alfilehandler.altext

import akka.actor.ActorRef
import com.pharbers.aqll.alcalc.alfilehandler.alFileHandler
import com.pharbers.aqll.calc.common.DefaultData.integratedXmlPath
import com.pharbers.aqll.calc.excel.IntegratedData.IntegratedData
import com.pharbers.aqll.calc.excel.core.rowinteractparser

/**
  * Created by Alfred on 09/03/2017.
  */
object alTextParser {
    def apply(path : String) : List[Any] = {
        val p = new alTextParser
        p.prase(path)("0")
        p.data.toList
    }
}

class alTextParser extends alFileHandler with CreateInnerParser {
    val parser = CreateInnerParser

    override def prase(path : String)(x : Any) : Any = {
        parser.startParse(path)
        this
    }
}

case class inner_parser(h : alFileHandler) {
    def startParse(file : String) = h.data.append(FileOpt(file).requestDataFromFile(x => x))
}

trait CreateInnerParser { this : alFileHandler =>
    def CreateInnerParser : inner_parser = new inner_parser(this)
}