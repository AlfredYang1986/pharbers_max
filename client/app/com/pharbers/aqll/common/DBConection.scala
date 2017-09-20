package com.pharbers.aqll.common

import com.pharbers.aqll.common.alDao.data_connection

object DBConection {
	import com.pharbers.aqll.common.alDao.dataFactory._
	import com.pharbers.aqll.common.alFileHandler.databaseConfig._
	
	val basic: data_connection = getDataBasic(dbhost, dbport.toInt, dbuser, dbpwd, db2)
	val cores: data_connection = getDataBasic(dbhost, dbport.toInt, dbuser, dbpwd, db2)
}
