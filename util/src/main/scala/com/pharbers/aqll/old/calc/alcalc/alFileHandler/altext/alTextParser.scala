package com.pharbers.aqll.old.calc.alcalc.alFileHandler.altext

import com.pharbers.aqll.old.calc.alcalc.alFileHandler.alFileHandlers


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

class alTextParser extends alFileHandlers with CreateInnerParser {
    val parser = CreateInnerParser

    override def prase(path : String)(x : Any) : Any = {
        parser.startParse(path)
        this
    }
}

case class inner_parser(h : alFileHandlers) {
    def startParse(file : String) = h.data.appendAll(FileOpt(file).requestDataFromFile(x => x))
}

trait CreateInnerParser { this : alFileHandlers =>
    def CreateInnerParser : inner_parser = new inner_parser(this)
}