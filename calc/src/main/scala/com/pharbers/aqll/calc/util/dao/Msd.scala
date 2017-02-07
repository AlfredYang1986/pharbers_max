package com.pharbers.aqll.calc.util.dao

import com.pharbers.aqll.calc.util.GetProperties

/**
  * Created by Wli on 2017/1/20.
  */
object Msd {
    /*def DBHost = GetProperties.loadProperties("msd.properties").getProperty("DBHost")
    def DBPort = Integer.parseInt( GetProperties.loadProperties("msd.properties").getProperty("DBPort"))
    def username = GetProperties.loadProperties("msd.properties").getProperty("username")
    def password = GetProperties.loadProperties("msd.properties").getProperty("password")
    def DB1 = GetProperties.loadProperties("msd.properties").getProperty("DB1")
    def DB2 = GetProperties.loadProperties("msd.properties").getProperty("DB2")*/

	def DBHost = GetProperties.loadConf("msd.conf").getString("DataBase.DBHost")
	def DBPort = GetProperties.loadConf("msd.conf").getInt("DataBase.DBPort")
	def username = GetProperties.loadConf("msd.conf").getString("DataBase.username")
	def password = GetProperties.loadConf("msd.conf").getString("DataBase.password")
	def DB1 = GetProperties.loadConf("msd.conf").getString("DataBase.DB1")
	def DB2 = GetProperties.loadConf("msd.conf").getString("DataBase.DB2")
}
