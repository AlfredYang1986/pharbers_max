package com.pharbers.aqll.excel.common

import com.pharbers.aqll.excel.core._
import com.pharbers.aqll.excel.model._

object ReadFileData {
    
    def hospdatadataobj(excel_file_name : String): List[Hospital] = {
        val xml_file_name = """file/xml/HospitalFields.xml"""
        val xml_file_name_ch = """file/xml/HospitalTitles.xml"""
        val hospdata_file_data = hospitalinteractparser(xml_file_name,xml_file_name_ch)
        hospdata_file_data.startParse(excel_file_name, 1)
        hospdata_file_data.resultlist
    }
    
    def productdataobj(excel_file_name : String): List[Products] = {
        val xml_file_name = """file/xml/ProductsFields.xml"""
        val xml_file_name_ch = """file/xml/ProductsTitles.xml"""
        val products_file_data = productsinteractparser(xml_file_name,xml_file_name_ch)
        products_file_data.startParse(excel_file_name, 1)
        products_file_data.resultlist
    }
    
}
