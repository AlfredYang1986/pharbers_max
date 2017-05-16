package com.pharbers.aqll.common.alFilePathConfig

import com.typesafe.config.ConfigFactory

/**
  * Created by liwei on 2017/5/15.
  */
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


