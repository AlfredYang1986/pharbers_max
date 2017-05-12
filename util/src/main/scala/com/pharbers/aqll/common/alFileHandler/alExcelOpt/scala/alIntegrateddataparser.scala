package com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala

import akka.actor.ActorRef
import com.pharbers.aqll.common.alFileHandler.alFileHandler
import com.pharbers.aqll.old.calc.alcalc.alCommon.DefaultData.integratedXmlPath
import com.pharbers.aqll.old.calc.alcalc.almodel.IntegratedData

class alIntegrateddataparser extends alFileHandler with CreateInnerParser {
    val parser = CreateInnerParser

    override def prase(path : String)(x : Any) : Any = {
        parser.startParse(path)
        this
    }
}

case class inner_parser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef, h : alFileHandler) extends rowinteractparser {
    type target_type = IntegratedData
    override def targetInstance = new IntegratedData
    override def handleOneTarget(target: target_type) = h.data.append(target)
}

trait CreateInnerParser { this : alFileHandler =>
    def CreateInnerParser : inner_parser =
        new inner_parser(integratedXmlPath.integratedxmlpath_en, integratedXmlPath.integratedxmlpath_ch, null, this)
}
