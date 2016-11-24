package com.pharbers.aqll.calc.excel.core

import akka.actor.ActorRef
import com.pharbers.aqll.calc.excel.Manage._
import com.pharbers.aqll.calc.excel.CPA._
import com.pharbers.aqll.calc.excel.PharmaTrust._

trait fileinteractparser extends interactparser {
	override def handleOneTarget(target : target_type) = Unit
}

case class hospdatainteractparser(xml_file_name : String, xml_file_name_ch : String) extends fileinteractparser {
	override val a : ActorRef = null
	type target_type = AdminHospitalDataBase
	override def targetInstance = new target_type
}

case class hospmatchinteractparse(xml_file_name: String, xml_file_name_ch: String) extends fileinteractparser {
    override val a: ActorRef = null
    type target_type = AdminHospitalMatchingData
    override def targetInstance = new target_type
}

case class marketinteractparse(xml_file_name: String, xml_file_name_ch: String) extends fileinteractparser {
    override val a: ActorRef = null
    type target_type = AdminMarket
    override def targetInstance = new target_type
}

case class productinteractparse(xml_file_name: String, xml_file_name_ch: String) extends fileinteractparser {
    override val a: ActorRef = null
    type target_type = AdminProduct
    override def targetInstance = new target_type
}

case class cpaproductinteractparse(xml_file_name: String, xml_file_name_ch: String) extends fileinteractparser {
    override val a: ActorRef = null
    type target_type = CpaProduct
    override def targetInstance = new target_type
}

case class cpamarketinteractparse(xml_file_name: String, xml_file_name_ch: String) extends fileinteractparser {
    override val a: ActorRef = null
    type target_type = CpaMarket
    override def targetInstance = new target_type
}

case class phaproductinteractparse(xml_file_name: String, xml_file_name_ch: String) extends fileinteractparser {
    override val a: ActorRef = null
    type target_type = PharmaTrustPorduct
    override def targetInstance = new target_type
}

case class phamarketinteractparse(xml_file_name: String, xml_file_name_ch: String) extends fileinteractparser {
    override val a: ActorRef = null
    type target_type = PharmaTrustMarket
    override def targetInstance = new target_type
}
