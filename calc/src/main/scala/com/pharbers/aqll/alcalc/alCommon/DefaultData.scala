package com.pharbers.aqll.alcalc.alCommon

import java.io.File

import com.pharbers.aqll.alcalc.alFileHandler.alexcel._
import com.pharbers.aqll.alcalc.alCommon.fileConfig

object DefaultData {
    def hospdatabase(path: String, company: String) = {
        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
        val hospdatabase = hospdatainteractparser(hospdata_en_file, hospdata_ch_file)
        hospdatabase.startParse(fileConfig.fileBase + company + fileConfig.hospitalData + path, 1)
        hospdatabase.resultlist
    }

    object integratedXmlPath {
        lazy val integratedxmlpath_ch = "config/consumer/IntegratedDataStruct.xml"
        lazy val integratedxmlpath_en = "config/consumer/FieldNamesIntegratedDataStruct.xml"
    }

    def integratedbase(filename: String, company: String) = {
        val integratedbase = integrateddataparser(integratedXmlPath.integratedxmlpath_en, integratedXmlPath.integratedxmlpath_ch)
        integratedbase.startParse(fileConfig.fileBase + company + fileConfig.outPut + filename, 1)
        integratedbase.resultlist
    }

    def FileFirst(path: String): String = {
        new File(path).listFiles().head.getPath
    }
}