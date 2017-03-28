package com.pharbers.aqll.util

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by Faiz on 2017/1/17.
  */
object GetProperties {

    import java.util.Properties
    import java.io.FileInputStream

    def loadProperties(filename: String): Properties = {
        val properties = new Properties()
        properties.load(new FileInputStream(Thread.currentThread().getContextClassLoader.getResource(filename).getPath))
        properties
    }

    def loadConf(filename: String): Config = {
        ConfigFactory.load(filename)
    }

    /*msd.conf*/
    def DBHost = GetProperties.loadConf("msd.conf").getString("DataBase.DBHost")

    def DBPort = GetProperties.loadConf("msd.conf").getInt("DataBase.DBPort")

    def username = GetProperties.loadConf("msd.conf").getString("DataBase.username")

    def password = GetProperties.loadConf("msd.conf").getString("DataBase.password")

    def DB1 = GetProperties.loadConf("msd.conf").getString("DataBase.DB1")

    def DB2 = GetProperties.loadConf("msd.conf").getString("DataBase.DB2")

    def Akka_Http_IP = loadConf("File.conf").getString("Akka.Http.ip")

    def Akka_Http_Port = loadConf("File.conf").getInt("Akka.Http.port")

    // TODO : Client上传／导出／Manage上传的HospitalData地址
    def fileBase = loadConf("File.conf").getString("File.FileBase_FilePath")

    def hospitalData = loadConf("File.conf").getString("File.Upload_HospitalData_File")

    def export_file = loadConf("File.conf").getString("File.Export_File")

    def transfer_file = loadConf("File.conf").getString("File.Transfer_File")

    def template_file = loadConf("File.conf").getString("File.Template_File")

    def client_cpa_file = loadConf("File.conf").getString("File.Client_Cpa")

    def client_gycx_file = loadConf("File.conf").getString("File.Client_Gycx")

    def manage_file = loadConf("File.conf").getString("File.Manage_File")

}

