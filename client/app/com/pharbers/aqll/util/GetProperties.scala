package com.pharbers.aqll.util

import java.io.FileInputStream
import java.util.Properties

import com.typesafe.config.ConfigFactory

/**
  * Created by clock on 2017/5/10.
  */
object GetProperties {
    def loadProperties(filename: String): Properties = {
        val properties = new Properties()
        properties.load(new FileInputStream(Thread.currentThread().getContextClassLoader.getResource(filename).getPath))
        properties
    }

    val msd: IPropertiesFactory = GetConfigFactory.getMsdConfigFactory
    val file: IPropertiesFactory = GetConfigFactory.getFileConfigFactory

    /*msd.conf*/
    val DBHost = msd.getProperties("DataBase.DBHost")
    val DBPort = msd.getProperties("DataBase.DBPort").toInt
    val username = msd.getProperties("DataBase.username")
    val password = msd.getProperties("DataBase.password")
    val DB1 = msd.getProperties("DataBase.DB1")
    val DB2 = msd.getProperties("DataBase.DB2")


    /*file.conf*/
    val Akka_Http_IP = file.getProperties("Akka.Http.ip")
    val Akka_Http_Port = file.getProperties("Akka.Http.port").toInt
    val fileBase = file.getProperties("File.FileBase_FilePath")
    val hospitalData = file.getProperties("File.Upload_HospitalData_File")
    val export_file = file.getProperties("File.Export_File")
    val transfer_file = file.getProperties("File.Transfer_File")
    val template_file = file.getProperties("File.Template_File")
    val client_cpa_file = file.getProperties("File.Client_Cpa")
    val client_gycx_file = file.getProperties("File.Client_Gycx")
    val manage_file = file.getProperties("File.Manage_File")

}

/**
  * The singleton abstract factory is used to generate the specified configuration file factory.
  */
object GetConfigFactory {
    val configFileMap = Map("mds" -> new MsdConfigFactory("msd.conf"), "file" -> new FileConfigFactory("File.conf"))

    def getMsdConfigFactory = {
        configFileMap("mds")
    }

    def getFileConfigFactory = {
        configFileMap("file")
    }
}

/**
  * Configure file factory interface
  */
trait IPropertiesFactory {
    def getProperties(configKey: String):String
}

class MsdConfigFactory(configFileName: String) extends IPropertiesFactory {
    val config = ConfigFactory.load(configFileName)

    override def getProperties(configKey: String):String = {
        if (configKey.equals("DataBase.DBPort")) {
            config.getInt(configKey).toString
        } else {
            config.getString(configKey)
        }
    }
}

class FileConfigFactory(configFileName: String) extends IPropertiesFactory {
    val config = ConfigFactory.load(configFileName)

    override def getProperties(configKey: String):String = {
        if (configKey.equals("Akka.Http.port")) {
            config.getInt(configKey).toString
        } else {
            config.getString(configKey)
        }
    }
}
