package com.pharbers.aqll.excel.common

import com.pharbers.aqll.excel.core._
import com.pharbers.aqll.excel.model._

object ReadFileData {
    
    def hospdatadataobj(file: String): List[Hospital] = {
        val fields = """file/xml/HospitalFields.xml"""
        val titles= """file/xml/HospitalTitles.xml"""
        val hospdata_file_data = hospitalinteractparser(fields,titles)
        hospdata_file_data.startParse(file, 1)
        hospdata_file_data.resultlist
    }
    
    def productdataobj(file: String): List[Products] = {
        val fields = """file/xml/ProductsFields.xml"""
        val titles= """file/xml/ProductsTitles.xml"""
        val products_file_data = productsinteractparser(fields,titles)
        products_file_data.startParse(file, 1)
        products_file_data.resultlist
    }
    
}