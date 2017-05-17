package com.pharbers.aqll.old.calc.alcalc.alFileHandler.alexcel

import akka.actor.ActorRef
import com.pharbers.aqll.old.calc.alcalc.alCommon.DefaultData.integratedXmlPath
import com.pharbers.aqll.old.calc.alcalc.alFileHandler.alFileHandlers
import com.pharbers.aqll.old.calc.alcalc.almodel.IntegratedData

/**
  * Created by Alfred on 09/03/2017.
  */
class alIntegrateddataparser extends alFileHandlers with CreateInnerParser {
    val parser = CreateInnerParser

    override def prase(path : String)(x : Any) : Any = {
        parser.startParse(path, 1)
        this
    }
}

case class inner_parser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef, h : alFileHandlers) extends rowinteractparser {
    type target_type = IntegratedData
    override def targetInstance = new IntegratedData
    override def handleOneTarget(target: target_type) = h.data.append(target)
}

trait CreateInnerParser { this : alFileHandlers =>
    def CreateInnerParser : inner_parser =
        new inner_parser(integratedXmlPath.integratedxmlpath_en, integratedXmlPath.integratedxmlpath_ch, null, this)
}
