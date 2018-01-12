package com.pharbers.aqll.alCalcHelp

import com.mongodb.casbah.MongoDB
import com.pharbers.panel.phPanelFilePath
import com.pharbers.aqll.common.alDao.dataFactory._
import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.aqll.common.alDao.data_connection
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import com.pharbers.aqll.common.alFileHandler.databaseConfig._
import com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala.alExcelDataParser
import com.pharbers.aqll.alCalcHelp.alModel.java.{AdminHospitalDataBase, IntegratedData}

object DefaultData {
    object market_file_path extends phPanelFilePath
    object file_path extends PharbersInjectModule {
        override val id: String = "calc-path"
        override val configPath: String = "pharbers_config/calc_path.xml"
        override val md = "bson-path" :: "hosp" :: "field-names-hosp" :: "integrated" :: "field-names-integrated" :: Nil

        val hosp = config.mc.find(p => p._1 == "hosp").get._2.toString
        val field_names_hosp = config.mc.find(p => p._1 == "field-names-hosp").get._2.toString
        val integrated = config.mc.find(p => p._1 == "integrated").get._2.toString
        val field_names_integrated = config.mc.find(p => p._1 == "field-names-integrated").get._2.toString
    }

    def hospdatabase(path: String, company: String, market: String): List[AdminHospitalDataBase] = {
        val hospdata_ch_file = file_path.hosp
        val hospdata_en_file = file_path.field_names_hosp
        type targt = AdminHospitalDataBase
        val hospdatabase = new alExcelDataParser(new targt, hospdata_en_file, hospdata_ch_file)

        val mkt_file_local = market_file_path.base_path + company + market_file_path.universe_inf_file.replace("##market##", market)
        hospdatabase.prase(mkt_file_local)("")
        hospdatabase.data.toList.asInstanceOf[List[targt]]
    }

    val integratedxmlpath_ch = file_path.integrated
    val integratedxmlpath_en = file_path.field_names_integrated

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

object dbAdmin extends DBList {
    override implicit val dbc: data_connection =  getDataAdmin(dbhost, dbport.toInt, dbuser, dbpwd)
    val dba: MongoDB = dbc._conn.getDB("admin")
}

object dbcores extends DBList {
    override implicit val dbc: data_connection =  getDataCores(dbhost, dbport.toInt, dbuser, dbpwd, db1)
}

object dbbasic extends DBList {
    override implicit val dbc: data_connection =  getDataBasic(dbhost, dbport.toInt, dbuser, dbpwd, db2)
}