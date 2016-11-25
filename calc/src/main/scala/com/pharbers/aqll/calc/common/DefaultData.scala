package com.pharbers.aqll.calc.common

import com.pharbers.aqll.calc.excel.core.hospdatainteractparser

object DefaultData {
    lazy val haspdatabase = {
        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
        val hospdata_file = """config/test/8000家taxol医院数据库表.xlsx"""
        val hospdatabase = hospdatainteractparser(hospdata_en_file, hospdata_ch_file)
        hospdatabase.startParse(hospdata_file, 1)
        hospdatabase.resultlist
    }
    
    // TODO: 有多少加多少	
    
    object capLoadXmlPath {
	    lazy val cpamarketxmlpath_ch = """config/consumer/CpaMarketDataStruct.xml"""
	    lazy val cpamarketxmlpath_en = """config/consumer/FieldNamesCpaMarketDataStruct.xml"""
	}
    
    // TODO: 有多少加多少
}