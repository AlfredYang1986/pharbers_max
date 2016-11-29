package com.pharbers.aqll.calc.common

import com.pharbers.aqll.calc.excel.core._

object DefaultData {
    lazy val hospdatabase = {
        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
        val hospdata_file = """config/test/8000家taxol医院数据库表.xlsx"""
        val hospdatabase = hospdatainteractparser(hospdata_en_file, hospdata_ch_file)
        hospdatabase.startParse(hospdata_file, 1)
        hospdatabase.resultlist
    }
    
    lazy val hospmatchdata = {
        val hosp_mathc_ch_file = """config/admin/HospDataMatchStruct.xml"""
        val hosp_match_en_file = """config/admin/FieldNamesHospMatchStruct.xml"""
        val hosp_match_file = """config/test/管理员维护_样本医院匹配表_2016_HTN_bpeng.xlsx"""
        val hospmatchdata = hospmatchinteractparser(hosp_match_en_file, hosp_mathc_ch_file)
        hospmatchdata.startParse(hosp_match_file, 1)
        hospmatchdata.resultlist
    }
    
    lazy val marketdata = {
        val market_ch_file = """config/admin/MarketDataStruct.xml"""
        val market_en_file = """config/admin/FieldNamesMarketDataStruct.xml"""
        val market_file = """config/test/管理员维护_市场匹配表_2016_HTN.xlsx"""
        val adminmarketdata = marketinteractparser(market_en_file, market_ch_file)
        adminmarketdata.startParse(market_file, 1)
        adminmarketdata.resultlist
    }
    
    lazy val productdata = {
        val product_ch_file = """config/admin/ProductDataStruct.xml"""
        val product_en_file = """config/admin/FieldNamesProductDataStruct.xml"""
        val product_file = """config/test/产品匹配表汇总.xlsx"""
        val adminproductdata = productinteractparser(product_en_file, product_ch_file)
        adminproductdata.startParse(product_file, 1)
        adminproductdata.resultlist
    }
    
    object capLoadXmlPath {
	    lazy val cpamarketxmlpath_ch = """config/consumer/CpaMarketDataStruct.xml"""
	    lazy val cpamarketxmlpath_en = """config/consumer/FieldNamesCpaMarketDataStruct.xml"""
	    lazy val cpaproductxmlpath_ch = """config/consumer/CpaProductDataStruct.xml"""
	    lazy val cpaproductxmlpath_en = """config/consumer/FieldNamesCpaProductDataStruct.xml"""
	}
    
    object phaLoadXmlPath {
        lazy val phamarketxmlpath_ch = """config/consumer/PhaMarketDataStruct.xml"""
        lazy val phamarketxmlpath_en = """config/consumer/FieldNamesPhaMarketDataStruct.xml"""
        lazy val phaproductxmlpath_ch = """config/consumer/PhaProductDataStruct.xml"""
        lazy val phaproductxmlpath_en = """config/consumer/FieldNamesPhaProductDataStruct.xml"""
    }
    
}