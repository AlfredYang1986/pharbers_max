package com.pharbers.aqll.calc.common

import com.pharbers.aqll.calc.excel.core._

import java.io.File

object DefaultData {
    lazy val hospdatabase = {
        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
        val hospdatabase = hospdatainteractparser(hospdata_en_file, hospdata_ch_file)
        hospdatabase.startParse(FileFirst("""D:\SourceData\Manage\医院数据"""), 1)
        hospdatabase.resultlist
    }
    
    lazy val hospmatchdata = {
        val hosp_mathc_ch_file = """config/admin/HospDataMatchStruct.xml"""
        val hosp_match_en_file = """config/admin/FieldNamesHospMatchStruct.xml"""
        val hospmatchdata = hospmatchinteractparser(hosp_match_en_file, hosp_mathc_ch_file)
        hospmatchdata.startParse(FileFirst("""D:\SourceData\Manage\医院匹配"""), 1)
        hospmatchdata.resultlist
    }
    
    lazy val marketdata = {
        val market_ch_file = """config/admin/MarketDataStruct.xml"""
        val market_en_file = """config/admin/FieldNamesMarketDataStruct.xml"""
        val adminmarketdata = marketinteractparser(market_en_file, market_ch_file)
        adminmarketdata.startParse(FileFirst("""D:\SourceData\Manage\市场匹配"""), 1)
        adminmarketdata.resultlist
    }
    
    lazy val productdata = {
        val product_ch_file = """config/admin/ProductDataStruct.xml"""
        val product_en_file = """config/admin/FieldNamesProductDataStruct.xml"""
        val adminproductdata = productinteractparser(product_en_file, product_ch_file)
        adminproductdata.startParse(FileFirst("""D:\SourceData\Manage\产品匹配"""), 1)
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

    def FileFirst(path: String): String = {
        new File(path).listFiles().head.getPath
    }
}