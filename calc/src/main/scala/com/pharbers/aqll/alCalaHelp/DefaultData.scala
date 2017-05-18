package com.pharbers.aqll.alCalaHelp

import com.pharbers.aqll.alCalc.almodel.java.{AdminHospitalDataBase, IntegratedData}
import com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala.alExcelDataParser
import com.pharbers.aqll.common.alFileHandler.fileConfig._

object DefaultData {
//    def hospdatabase(path: String, company: String) = {
//        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
//        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
//        val hospdatabase = hospdatainteractparser(hospdata_en_file, hospdata_ch_file)
//        hospdatabase.startParse(fileBase + company + hospitalData + path, 1)
//        hospdatabase.resultlist
//    }

    def hospdatabase(path: String, company: String) = {
        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
        type targt = AdminHospitalDataBase
        val hospdatabase = new alExcelDataParser(new targt, hospdata_en_file, hospdata_ch_file)
        hospdatabase.prase(fileBase + company + hospitalData + path)("")
        hospdatabase.data.toList.asInstanceOf[List[AdminHospitalDataBase]]
    }

//    object integratedXmlPath {
//        lazy val integratedxmlpath_ch = "config/consumer/IntegratedDataStruct.xml"
//        lazy val integratedxmlpath_en = "config/consumer/FieldNamesIntegratedDataStruct.xml"
//    }

//    def integratedbase(filename: String, company: String) = {
//        val integratedbase = integrateddataparser(integratedXmlPath.integratedxmlpath_en, integratedXmlPath.integratedxmlpath_ch)
//        integratedbase.startParse(fileBase + company + outPut + filename, 1)
//        integratedbase.resultlist
//    }

    val integratedxmlpath_ch = "config/consumer/IntegratedDataStruct.xml"
    val integratedxmlpath_en = "config/consumer/FieldNamesIntegratedDataStruct.xml"

    def integratedbase(filename: String, company: String) = {
        type targt = IntegratedData
        val integratedbase = new alExcelDataParser(new targt, integratedxmlpath_en, integratedxmlpath_ch)
        integratedbase.prase(fileBase + company + outPut + filename)("")
        integratedbase.data.asInstanceOf[List[targt]]
    }
}