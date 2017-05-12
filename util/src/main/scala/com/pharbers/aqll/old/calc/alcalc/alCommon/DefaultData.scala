package com.pharbers.aqll.old.calc.alcalc.alCommon

import java.io.File

import com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala.{alExcelDataParser, exceldataparser}
import com.pharbers.aqll.old.calc.alcalc.almodel.AdminHospitalData
import com.pharbers.aqll.old.calc.alcalc.alFileHandler.alexcel._
import com.pharbers.aqll.old.calc.util.GetProperties

object DefaultData {
    def hospdatabase(path: String, company: String) = {
        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
        val hospdatabase = hospdatainteractparser(hospdata_en_file, hospdata_ch_file)
        hospdatabase.startParse(GetProperties.fileBase + company + GetProperties.hospitalData + path, 1)
        hospdatabase.resultlist
    }


    def hospdatabasetest2(path: String) = {
        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
        type targt = AdminHospitalData
        val a = new alExcelDataParser(new targt, hospdata_en_file, hospdata_ch_file)
        a.prase(path)("")
        a.data

    }


    object integratedXmlPath {
        lazy val integratedxmlpath_ch = "config/consumer/IntegratedDataStruct.xml"
        lazy val integratedxmlpath_en = "config/consumer/FieldNamesIntegratedDataStruct.xml"
    }

    def integratedbase(filename: String, company: String) = {
        val integratedbase = integrateddataparser(integratedXmlPath.integratedxmlpath_en, integratedXmlPath.integratedxmlpath_ch)
        integratedbase.startParse(GetProperties.fileBase + company + GetProperties.outPut + filename, 1)
        integratedbase.resultlist
    }

    def FileFirst(path: String): String = {
        new File(path).listFiles().head.getPath
    }
}