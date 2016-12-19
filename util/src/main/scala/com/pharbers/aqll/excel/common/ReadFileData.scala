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
    
    def segmentdataobj(excel_file_name : String): List[SegmentInfo] = {
        val xml_file_name = """file/xml/SegmentFields.xml"""
        val xml_file_name_ch = """file/xml/SegmentTitles.xml"""
        val data = segmentInfointeractparser(xml_file_name,xml_file_name_ch)
        data.startParse(excel_file_name, 1)
        data.resultlist
    }
    
    def atccodedataobj(excel_file_name : String): List[AtcCode] = {
        val xml_file_name = """file/xml/AtcCodeFields.xml"""
        val xml_file_name_ch = """file/xml/AtcCodeTitles.xml"""
        val data = atccodeinteractparser(xml_file_name,xml_file_name_ch)
        data.startParse(excel_file_name, 1)
        data.resultlist
    }
    
        
    def durgdataobj(excel_file_name : String): List[Durg] = {
        val xml_file_name = """file/xml/DurgFields.xml"""
        val xml_file_name_ch = """file/xml/DurgTitles.xml"""
        val data = durginteractparser(xml_file_name,xml_file_name_ch)
        data.startParse(excel_file_name, 1)
        data.resultlist
    }
    
        
    def routeofmedicationdataobj(excel_file_name : String): List[Routeofmedication] = {
        val xml_file_name = """file/xml/RouteofMedicationFields.xml"""
        val xml_file_name_ch = """file/xml/RouteofMedicationTitles.xml"""
        val data = routeofminteractparser(xml_file_name,xml_file_name_ch)
        data.startParse(excel_file_name, 1)
        data.resultlist
    }
    
        
    def segmentbasicdataobj(excel_file_name : String): List[SegmentBasic] = {
        val xml_file_name = """file/xml/SegmentbasicFields.xml"""
        val xml_file_name_ch = """file/xml/SegmentbasicTitles.xml"""
        val data = segmentBasicinteractparser(xml_file_name,xml_file_name_ch)
        data.startParse(excel_file_name, 1)
        data.resultlist
    }
}
