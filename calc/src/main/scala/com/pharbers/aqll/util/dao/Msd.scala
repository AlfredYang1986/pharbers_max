package com.pharbers.aqll.util.dao

import com.pharbers.aqll.util.GetProperties

/**
  * Created by Wli on 2017/1/20.
  */
object Msd {
	def DBHost = GetProperties.loadConf("msd.conf").getString("DataBase.DBHost")
	def DBPort = GetProperties.loadConf("msd.conf").getInt("DataBase.DBPort")
	def username = GetProperties.loadConf("msd.conf").getString("DataBase.username")
	def password = GetProperties.loadConf("msd.conf").getString("DataBase.password")
	def DB1 = GetProperties.loadConf("msd.conf").getString("DataBase.DB1")
	def DB2 = GetProperties.loadConf("msd.conf").getString("DataBase.DB2")
}
