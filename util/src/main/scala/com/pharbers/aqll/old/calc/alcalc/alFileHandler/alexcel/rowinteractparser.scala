package com.pharbers.aqll.old.calc.alcalc.alFileHandler.alexcel

import akka.actor.ActorRef
import com.pharbers.aqll.old.calc.alcalc.almodel.IntegratedData

case class integratedresult(t: IntegratedData)

trait rowinteractparser extends interactparser {
	override def handleOneTarget(target : target_type) = a ! target
}

case class row_integrateddataparser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef) extends rowinteractparser {
    type target_type = IntegratedData
  override def targetInstance = new IntegratedData
  override def handleOneTarget(target : target_type) = a ! integratedresult(target)
}