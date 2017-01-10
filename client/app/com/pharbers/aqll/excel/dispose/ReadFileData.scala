package com.pharbers.aqll.excel.dispose

import com.pharbers.aqll.excel.core._
import com.pharbers.aqll.excel.model._

object ReadFileData {
    def hospitaldata(excel_file_name : String): List[AdminHospitalDataBase] = {
        val xml_file_name = """xml/FieldNamesHospDataStruct.xml"""
        val xml_file_name_ch = """xml/HospDataStruct.xml"""
        val hospdata_file_data = hospdatainteractparser(xml_file_name,xml_file_name_ch)
        hospdata_file_data.startParse(excel_file_name, 1)
        hospdata_file_data.resultlist
    }
    
    def hospitalmatchdata(excel_file_name : String): List[AdminHospitalMatchingData] = {
        val xml_file_name = """xml/FieldNamesHospMatchStruct.xml"""
        val xml_file_name_ch = """xml/HospDataMatchStruct.xml"""
        val products_file_data = hospmatchinteractparser(xml_file_name,xml_file_name_ch)
        products_file_data.startParse(excel_file_name, 1)
        products_file_data.resultlist
    }
    
    def marketmatchdata(excel_file_name : String): List[AdminMarket] = {
        val xml_file_name = """xml/FieldNamesMarketDataStruct.xml"""
        val xml_file_name_ch = """xml/MarketDataStruct.xml"""
        val data = marketinteractparser(xml_file_name,xml_file_name_ch)
        data.startParse(excel_file_name, 1)
        data.resultlist
    }
    
    def productmatchdata(excel_file_name : String): List[AdminProduct] = {
        val xml_file_name = """xml/FieldNamesProductDataStruct.xml"""
        val xml_file_name_ch = """xml/ProductDataStruct.xml"""
        val data = productinteractparser(xml_file_name,xml_file_name_ch)
        data.startParse(excel_file_name, 1)
        data.resultlist
    }
}
