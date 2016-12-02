package com.pharbers.aqll.excel.common

import com.pharbers.aqll.excel.core._
import com.pharbers.aqll.excel.model._

object ReadFileData {
    
    def hospdatadataobj(excel_file_name : String, xml_file_name : String, xml_file_name_ch : String): List[Hospital] = {
        val hospdata_file_data = hospitalinteractparser(xml_file_name,xml_file_name_ch)
        hospdata_file_data.startParse(excel_file_name, 1)
        hospdata_file_data.resultlist
    }
    
    def productdataobj(excel_file_name : String, xml_file_name : String, xml_file_name_ch : String): List[Products] = {
        val products_file_data = productsinteractparser(xml_file_name,xml_file_name_ch)
        products_file_data.startParse(excel_file_name, 1)
        products_file_data.resultlist
    }
    
}
