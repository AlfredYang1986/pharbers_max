package com.pharbers.aqll.util

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by Faiz on 2017/1/17.
  */
object GetProperties {
    import java.util.Properties
    import java.io.FileInputStream

    def loadProperties(filename: String) : Properties = {
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

    /*File.conf*/
    def Client_Upload_FilePath = loadConf("File.conf").getString("Files.UpClient_File_Path")
    def Client_Download_FilePath = loadConf("File.conf").getString("Files.DownClient_Temp_FilePath")
    def Client_Export_FilePath = loadConf("File.conf").getString("Files.DownClient_Export_FilePath")
    def UpManage_Upload_FilePath = loadConf("File.conf").getString("Files.UpManage_Upload_FilePath")


    /*客户上传*/
    def Upload_CPA_Product_FilePath = loadConf("File.conf").getString("Files.Upload_CPA_Product_FilePath")
    def Upload_CPA_Market_FilePath = loadConf("File.conf").getString("Files.Upload_CPA_Market_FilePath")
    def Upload_PT_Product_FilePath = loadConf("File.conf").getString("Files.Upload_PT_Product_FilePath")
    def Upload_PT_Market_FilePath = loadConf("File.conf").getString("Files.Upload_PT_Market_FilePath")

    def Akka_Http_IP = loadConf("File.conf").getString("Akka.Http.ip")
    def Akka_Http_Port = loadConf("File.conf").getInt("Akka.Http.port")
}
