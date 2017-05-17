package com.pharbers.aqll.common

import com.typesafe.config.ConfigFactory

/**
  * Created by liwei on 2017/5/15.
  */


object databaseConfig {
  val databaseobj : alDataBaseFileConfig = new alDataBaseFileConfig()
  val DBHost = databaseobj.getProperties("database.dbhost")
  val DBPort = databaseobj.getProperties("database.dbport").toInt
  val username = databaseobj.getProperties("database.username")
  val password = databaseobj.getProperties("database.password")
  val DB1 = databaseobj.getProperties("database.db1")
  val DB2 = databaseobj.getProperties("database.db2")
}

object akkaConfig {
  val akkaobj : alAkkaFileConfig = new alAkkaFileConfig()
  val Akka_Http_IP = akkaobj.getProperties("akka.http.ip")
  val Akka_Http_Port = akkaobj.getProperties("akka.http.port").toInt
}

object fopConfig {
  val fopobj : alFopFileConfig = new alFopFileConfig()
  val fileBase = fopobj.getProperties("fop.filebase")
  val hospitalData = fopobj.getProperties("fop.hospital")
  val export_file = fopobj.getProperties("fop.export")
  val client_cpa_file = fopobj.getProperties("fop.cpa")
  val client_gycx_file = fopobj.getProperties("fop.gycx")
  val manage_file = fopobj.getProperties("fop.manage")
}

trait alFilePathConfigTrait {
  def getProperties(configKey: String) : String
}

class alDataBaseFileConfig() extends alFilePathConfigTrait{
  val config = ConfigFactory.load("database.conf")

  override def getProperties(key: String):String = {
    key match {
      case "database.dbport" => config.getInt(key).toString
      case _ => config.getString(key)
    }
  }
}

class alAkkaFileConfig() extends alFilePathConfigTrait{
  val config = ConfigFactory.load("akka.conf")

  override def getProperties(key: String):String = {
    key match {
      case "akka.http.port" => config.getInt(key).toString
      case _ => config.getString(key)
    }
  }
}

class alFopFileConfig() extends alFilePathConfigTrait{
  val config = ConfigFactory.load("fop.conf")

  override def getProperties(key: String):String = {
    config.getString(key)
  }
}


