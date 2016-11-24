package com.pharbers.aqll.calc.excel.core

import akka.actor.ActorRef

trait rowinteractparser extends interactparser {
	override def handleOneTarget(target : target_type) = a ! target
}

case class cpainteractparser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef) extends rowinteractparser {
	import com.pharbers.aqll.calc.excel.CPA.CpaProduct
	type target_type = CpaProduct
	override def targetInstance = new CpaProduct
}

case class cpamarketinteractparser(xml_file_name : String, xml_file_name_ch : String, a : ActorRef) extends rowinteractparser {
	import com.pharbers.aqll.calc.excel.CPA.CpaMarket
	type target_type = CpaMarket
	override def targetInstance = new CpaMarket
}

// TODO : 有多少加多少