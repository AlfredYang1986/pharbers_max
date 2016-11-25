package com.pharbers.aqll.calc.excel.core

import akka.actor.ActorRef
import com.pharbers.aqll.calc.excel.CPA._
import com.pharbers.aqll.calc.excel.PharmaTrust._

case class cparesult(t : CpaProduct)
case class cpamarketresult(t : CpaMarket)
case class pharesult(t : PharmaTrustMarket)
case class phamarketresult(t : PharmaTrustPorduct)

trait rowinteractparser extends interactparser {
	override def handleOneTarget(target : target_type) = a ! target
}

case class cpaproductinteractparser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef) extends rowinteractparser {
	type target_type = CpaProduct
	override def targetInstance = new CpaProduct
	
	override def handleOneTarget(target : target_type) = a ! cparesult(target)
}

case class cpamarketinteractparser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef) extends rowinteractparser {
	type target_type = CpaMarket
	override def targetInstance = new CpaMarket

	override def handleOneTarget(target : target_type) = a ! cpamarketresult(target)
}

case class phaproductinteractparser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef) extends rowinteractparser {
    type target_type = PharmaTrustMarket
	override def targetInstance = new PharmaTrustMarket
	
	override def handleOneTarget(target : target_type) = a ! pharesult(target)
}

case class  phamarketinteractparser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef) extends rowinteractparser {
    type target_type = PharmaTrustPorduct
    override def targetInstance = new PharmaTrustPorduct
	
	override def handleOneTarget(target : target_type) = a ! phamarketresult(target)
}