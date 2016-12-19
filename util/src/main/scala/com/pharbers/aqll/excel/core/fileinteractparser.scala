package com.pharbers.aqll.excel.core

import com.pharbers.aqll.excel.model._

trait fileinteractparser extends interactparser {
	override def handleOneTarget(target : target_type) = Unit
}

case class hospitalinteractparser(xml_file_name : String, xml_file_name_ch : String) extends fileinteractparser {
	type target_type = Hospital
	override def targetInstance = new target_type
}

case class productsinteractparser(xml_file_name : String, xml_file_name_ch : String) extends fileinteractparser {
	type target_type = Products
	override def targetInstance = new target_type
}

case class segmentInfointeractparser(xml_file_name : String, xml_file_name_ch : String) extends fileinteractparser {
	type target_type = SegmentInfo
	override def targetInstance = new target_type
}

case class atccodeinteractparser(xml_file_name : String, xml_file_name_ch : String) extends fileinteractparser {
	type target_type = AtcCode
	override def targetInstance = new target_type
}

case class durginteractparser(xml_file_name : String, xml_file_name_ch : String) extends fileinteractparser {
	type target_type = Durg
	override def targetInstance = new target_type
}

case class routeofminteractparser(xml_file_name : String, xml_file_name_ch : String) extends fileinteractparser {
	type target_type = Routeofmedication
	override def targetInstance = new target_type
}

case class segmentBasicinteractparser(xml_file_name : String, xml_file_name_ch : String) extends fileinteractparser {
	type target_type = SegmentBasic
	override def targetInstance = new target_type
}