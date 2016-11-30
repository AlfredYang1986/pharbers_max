package com.pharbers.aqll.excel.core

import com.pharbers.aqll.excel.model.Hospital
import com.pharbers.aqll.excel.model.Products

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
