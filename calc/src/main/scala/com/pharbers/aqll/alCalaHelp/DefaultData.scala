package com.pharbers.aqll.alCalaHelp

import com.pharbers.aqll.alCalc.almodel.java.{AdminHospitalDataBase, IntegratedData}
import com.pharbers.aqll.common.alDao.dataFactory._
import com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala.alExcelDataParser
import com.pharbers.aqll.common.alFileHandler.fileConfig._

object DefaultData {

    def hospdatabase(path: String, company: String) = {
        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
        type targt = AdminHospitalDataBase
        val hospdatabase = new alExcelDataParser(new targt, hospdata_en_file, hospdata_ch_file)
        hospdatabase.prase(fileBase + company + hospitalData + path)("")
        hospdatabase.data.toList.asInstanceOf[List[AdminHospitalDataBase]]
    }

    val integratedxmlpath_ch = "config/consumer/IntegratedDataStruct.xml"
    val integratedxmlpath_en = "config/consumer/FieldNamesIntegratedDataStruct.xml"

    def integratedbase(filename: String, company: String) = {
        type targt = IntegratedData
        val integratedbase = new alExcelDataParser(new targt, integratedxmlpath_en, integratedxmlpath_ch)
        integratedbase.prase(fileBase + company + outPut + filename)("")
        integratedbase.data.toList.asInstanceOf[List[targt]]
    }
}

trait DBList {
    // TODO 这个地方需要读取配置文件，如果都是默认的可以忽略，最好读取配置文件
    val dbcores = getDataCores()
    val dbbasic = gerDataBasic()
}