package com.pharbers.aqll.alCalaHelp

import com.pharbers.aqll.alCalc.almodel.java.{AdminHospitalDataBase, IntegratedData}
import com.pharbers.aqll.common.alDao.dataFactory._
import com.pharbers.aqll.common.alDao.data_connection
import com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala.alExcelDataParser
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.databaseConfig._

object DefaultData {

    def hospdatabase(path: String, company: String): List[AdminHospitalDataBase] = {
        val hospdata_ch_file = "config/admin/HospDataStruct.xml"
        val hospdata_en_file = "config/admin/FieldNamesHospDataStruct.xml"
        type targt = AdminHospitalDataBase
        val hospdatabase = new alExcelDataParser(new targt, hospdata_en_file, hospdata_ch_file)
//        hospdatabase.prase(fileBase + company + hospitalData + path)("")
        hospdatabase.prase("config/FileBase/tekken")("")
        hospdatabase.data.toList.asInstanceOf[List[targt]]
    }

    val integratedxmlpath_ch = "config/consumer/IntegratedDataStruct.xml"
    val integratedxmlpath_en = "config/consumer/FieldNamesIntegratedDataStruct.xml"

    def integratedbase(filename: String, company: String): List[IntegratedData] = {
        type targt = IntegratedData
        val integratedbase = new alExcelDataParser(new targt, integratedxmlpath_en, integratedxmlpath_ch)
        integratedbase.prase(fileBase + company + outPut + filename)("")
        integratedbase.data.toList.asInstanceOf[List[targt]]
    }
}

trait DBList {
    implicit val dbc: data_connection
}

// TODO 这个地方需要读取配置文件，如果都是默认的可以忽略，最好读取配置文件
object dbcores extends DBList {
    override implicit val dbc: data_connection =  getDataCores(dbhost, dbport.toInt, dbuser, dbpwd, db1)
}

// TODO 这个地方需要读取配置文件，如果都是默认的可以忽略，最好读取配置文件
object dbbasic extends DBList {
    override implicit val dbc: data_connection =  getDataBasic(dbhost, dbport.toInt, dbuser, dbpwd, db2)
}