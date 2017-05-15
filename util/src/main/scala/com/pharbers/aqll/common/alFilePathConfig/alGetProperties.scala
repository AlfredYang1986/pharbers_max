package com.pharbers.aqll.common.alFilePathConfig

/**
  * Created by liwei on 2017/5/15.
  */
object alGetProperties {
  val databaseobj : alDataBaseFileConfig = new alDataBaseFileConfig()
  val DBHost = databaseobj.getProperties("database.dbhost")
  val DBPort = databaseobj.getProperties("database.dbport").toInt
  val username = databaseobj.getProperties("database.username")
  val password = databaseobj.getProperties("database.password")
  val DB1 = databaseobj.getProperties("database.db1")
  val DB2 = databaseobj.getProperties("database.db2")

  val akkaobj : alAkkaFileConfig = new alAkkaFileConfig()
  val Akka_Http_IP = akkaobj.getProperties("akka.http.ip")
  val Akka_Http_Port = akkaobj.getProperties("akka.http.port").toInt

  val fopobj : alFopFileConfig = new alFopFileConfig()
  val fileBase = fopobj.getProperties("fop.filebase")
  val hospitalData = fopobj.getProperties("fop.hospital")
  val export_file = fopobj.getProperties("fop.export")
  val client_cpa_file = fopobj.getProperties("fop.cpa")
  val client_gycx_file = fopobj.getProperties("fop.gycx")
  val manage_file = fopobj.getProperties("fop.manage")
}
