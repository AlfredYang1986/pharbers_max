package com.pharbers.aqll.old.client.util.dao

import com.pharbers.aqll.old.client.util.GetProperties


/**
  * Created by Wli on 2017/1/20.
  */
object Msd {
    def DBHost = GetProperties.loadProperties("msd.properties").getProperty("DBHost")
    def DBPort = Integer.parseInt( GetProperties.loadProperties("msd.properties").getProperty("DBPort"))
    def username = GetProperties.loadProperties("msd.properties").getProperty("username")
    def password = GetProperties.loadProperties("msd.properties").getProperty("password")
    def DB1 = GetProperties.loadProperties("msd.properties").getProperty("DB1")
    def DB2 = GetProperties.loadProperties("msd.properties").getProperty("DB2")
}
